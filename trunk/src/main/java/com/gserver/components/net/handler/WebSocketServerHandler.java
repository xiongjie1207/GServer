package com.gserver.components.net.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gserver.components.session.ISession;
import com.gserver.core.CommanderGroup;
import com.gserver.core.GameCons;
import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.utils.Loggers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

@ChannelHandler.Sharable
public class WebSocketServerHandler extends SocketServerHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketServerHandshaker handshaker;

    public WebSocketServerHandler() {
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg)
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
            Loggers.PacketLogger.debug(jsonObject.toString());
        } catch (Exception e) {
            Loggers.ErrorLogger.error(e.getMessage(), e.getCause());
        }
        if (jsonObject == null) {

            return;
        }
        int pid =Integer.parseInt(jsonObject.get(GameCons.PID).toString());
        IPacket packet = Packet.newNetBuilder(pid).setData(request.getBytes()).build();
        AttributeKey<ISession> key = AttributeKey.valueOf(GameCons.SessionAttrKey);
        ISession session = ctx.channel().attr(key).get();
        CommanderGroup.getInstance().dispatch(packet, session);
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

}