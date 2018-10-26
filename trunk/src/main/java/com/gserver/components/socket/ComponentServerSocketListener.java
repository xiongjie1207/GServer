package com.gserver.components.socket;

import com.gserver.codec.CustomZLibDecoder;
import com.gserver.codec.CustomZLibEncoder;
import com.gserver.codec.MessageDecoder;
import com.gserver.codec.MessageEncode;
import com.gserver.components.IComponent;
import com.gserver.config.ServerConfig;
import com.gserver.core.Commanders;
import com.gserver.core.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2017/1/11.
 */

/**
 * 所有数据流传输采用json格式
 * 其中pid是必须要有的，用来指定协议
 * 基于tcp/ip的数据传输
 * 数据流发送格式{"pid":1,"name":"guest","password":"111111","id":1,"clientType":0 }
 * 数据流接收格式{"pid":1,"name":"guest","password":"111111","id":1,"clientType":0 }
 */
public abstract class ComponentServerSocketListener implements IComponent {
    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    protected ServerBootstrap serverBootstrap;
    protected EventExecutorGroup eventExecutorGroup;
    private Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
    private Map<ChannelOption<?>, Object> childOptionObjectMap = new HashMap<>();

    private Logger logger = Logger.getLogger(this.getClass());
    private Thread t;

    @Override
    public boolean start() {
        t = new Thread(() -> this.runServer());
        t.start();
        return true;
    }

    @Override
    public boolean stop() {
        try {
            bossGroup.shutdownGracefully().await();
            workerGroup.shutdownGracefully().await();
            eventExecutorGroup.shutdownGracefully().await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        return true;
    }

    public String getName() {
        return "ComponentServerSocketListener";
    }

    private void runServer() {
        try {

            serverBootstrap = new ServerBootstrap();
            initConfig(ServerConfig.getInstance());
            if (ServerConfig.getInstance().getBossCount() == 0) {
                bossGroup = new NioEventLoopGroup();
            } else {
                bossGroup = new NioEventLoopGroup(ServerConfig.getInstance().getBossCount());
            }
            if (ServerConfig.getInstance().getWorkerCount() == 0) {
                workerGroup = new NioEventLoopGroup();
            } else {
                workerGroup = new NioEventLoopGroup(ServerConfig.getInstance().getWorkerCount());
            }
            eventExecutorGroup = new DefaultEventExecutorGroup(ServerConfig.getInstance().getHandlerTreadCount());
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(getChannelInitializer());
            initOption(optionObjectMap);
            for (Object key : optionObjectMap.keySet()) {
                serverBootstrap.option((ChannelOption<Object>) key, optionObjectMap.get(key));
            }
            initChildOption(childOptionObjectMap);
            for (Object key : childOptionObjectMap.keySet()) {
                serverBootstrap.childOption((ChannelOption<Object>) key, childOptionObjectMap.get(key));
            }
            serverBootstrap.localAddress(new InetSocketAddress(getPort()));

            channelFuture = serverBootstrap.bind().addListener((ChannelFuture future) -> this.operationComplete(future));

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private void operationComplete(ChannelFuture future) throws Exception {
        // TODO Auto-generated method stub
        if (future.isSuccess()) {
            logger.debug("[GServer] Started success");
        } else {
            logger.error("[GServer] Started Failed");
        }
    }

    protected void initConfig(ServerConfig config) {

    }

    protected void initOption(Map<ChannelOption<?>, Object> config) {
    }

    protected void initChildOption(Map<ChannelOption<?>, Object> config) {
    }


    private class GameServerChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel socketChannel) throws Exception {
            // TODO Auto-generated method stub
            //接收数据通道
            //指定数据长度
            socketChannel.pipeline().addFirst(LengthFieldBasedFrameDecoder.class.getSimpleName(),
                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            //对数据解压缩
            socketChannel.pipeline().addAfter(LengthFieldBasedFrameDecoder.class.getSimpleName(),
                    CustomZLibDecoder.class.getSimpleName(), new CustomZLibDecoder());
            //对数据解码成string
            socketChannel.pipeline().addAfter(CustomZLibDecoder.class.getSimpleName(),
                    StringDecoder.class.getSimpleName(), new StringDecoder(CharsetUtil.UTF_8));
            //解码成java对象
            socketChannel.pipeline().addAfter(StringDecoder.class.getSimpleName(),
                    MessageDecoder.class.getSimpleName(), new MessageDecoder());
            //////////////////////////////////////////////////////////////////////////////////
            //发送数据通道
            //添加数据长度
            socketChannel.pipeline().addAfter(MessageDecoder.class.getSimpleName(),
                    LengthFieldPrepender.class.getSimpleName(), new LengthFieldPrepender(4));
            //对数据压缩
            socketChannel.pipeline().addAfter(LengthFieldPrepender.class.getSimpleName(),
                    CustomZLibEncoder.class.getSimpleName(), new CustomZLibEncoder());
            //对数据进行string编码
            socketChannel.pipeline().addAfter(CustomZLibEncoder.class.getSimpleName(),
                    StringEncoder.class.getSimpleName(), new StringEncoder(CharsetUtil.UTF_8));
            //对java对象编码
            socketChannel.pipeline().addAfter(StringEncoder.class.getSimpleName(),
                    MessageEncode.class.getSimpleName(), new MessageEncode());
            IdleStateHandler idleStateHandler = new IdleStateHandler(ServerConfig.getInstance().getReaderIdleTimeSeconds(), ServerConfig.getInstance().getWriterIdleTimeSeconds(), ServerConfig.getInstance().getAllIdleTimeSeconds(), TimeUnit.SECONDS);
            socketChannel.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
            MessageHandler handler = new MessageHandler();
            socketChannel.pipeline().addLast(eventExecutorGroup, handler);
        }
    }

    protected short getPort() {
        return ServerConfig.getInstance().getPort();
    }

    @ChannelHandler.Sharable
    protected class MessageHandler extends ChannelInboundHandlerAdapter {
        public MessageHandler() {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Packet packet = new Packet((Map<String, Object>) msg);
            Commanders.getInstance().dispatch(packet, ctx.channel());
            ReferenceCountUtil.release(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            logger.debug("==============channel-read-complete==============");
            ctx.flush();
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