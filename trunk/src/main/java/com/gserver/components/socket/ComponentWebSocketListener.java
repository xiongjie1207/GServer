package com.gserver.components.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gserver.config.ServerConfig;
import com.gserver.core.Commanders;
import com.gserver.core.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

public abstract class ComponentWebSocketListener extends ComponentServerSocketListener {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private class GameServerChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast("http-decorder", new HttpRequestDecoder());
            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
            IdleStateHandler idleStateHandler = new IdleStateHandler(ServerConfig.getInstance().getReaderIdleTimeSeconds(), ServerConfig.getInstance().getWriterIdleTimeSeconds(), ServerConfig.getInstance().getAllIdleTimeSeconds(), TimeUnit.SECONDS);
            ch.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
            WebSocketServerHandler handler = new WebSocketServerHandler();
            ch.pipeline().addLast(eventExecutorGroup, handler);
        }
    }

    @Override
    protected short getPort() {
        return ServerConfig.getInstance().getWebSocketPort();
    }

    @ChannelHandler.Sharable
    public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
        private ObjectMapper objectMapper = new ObjectMapper();
        private WebSocketServerHandshaker handshaker;

        public WebSocketServerHandler() {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            // TODO Auto-generated method stub
            logger.debug("==============channel-read-complete==============");
            ctx.flush();
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            // TODO Auto-generated method stub

            if (msg instanceof FullHttpRequest) {

                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
            }


        }


        private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

            //关闭请求
            if (frame instanceof CloseWebSocketFrame) {

                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());

                return;
            }
            //ping请求
            if (frame instanceof PingWebSocketFrame) {

                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));

                return;
            }
            //只支持文本格式，不支持二进制消息
            if (!(frame instanceof TextWebSocketFrame)) {

                throw new Exception("仅支持文本格式");
            }

            //客户端发送过来的消息
            String request = ((TextWebSocketFrame) frame).text();
            Map<String, Object> jsonObject = null;
            try {
                jsonObject = objectMapper.readValue(request, Map.class);
                logger.debug(jsonObject);
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
            }
            if (jsonObject == null) {

                return;
            }

            Packet packet = new Packet(jsonObject);
            Commanders.getInstance().dispatch(packet, ctx.channel());
            ReferenceCountUtil.release(frame);

        }

        //第一次请求是http请求，请求头包括ws的信息
        private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {


            if (!req.decoderResult().isSuccess()) {

                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }

            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws:/" + ctx.channel() + "/websocket", null, false);
            handshaker = wsFactory.newHandshaker(req);


            if (handshaker == null) {
                //不支持
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {

                handshaker.handshake(ctx.channel(), req);
            }

        }


        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {


            // 返回应答给客户端
            if (res.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
                res.content().writeBytes(buf);
                buf.release();
            }

            // 如果是非Keep-Alive，关闭连接
            ChannelFuture f = ctx.channel().writeAndFlush(res);
            if (!isKeepAlive(req) || res.status().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (cause instanceof IOException && ctx.channel().isActive()) {
                logger.error("simpleclient" + ctx.channel().remoteAddress() + "异常");
            }
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                switch (e.state()) {
                    case ALL_IDLE:
                        break;
                    case READER_IDLE:
                        logger.info("写空闲:" + ctx.channel());
                        ctx.channel().disconnect();
                        ctx.channel().close();
                        break;
                    case WRITER_IDLE:
                        break;
                }

            }
        }

    }

}
