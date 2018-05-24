package com.gserver.plugins.socket;

import com.gserver.codec.CustomZLibDecoder;
import com.gserver.codec.CustomZLibEncoder;
import com.gserver.codec.MessageDecoder;
import com.gserver.codec.MessageEncode;
import com.gserver.config.ClientConfig;
import com.gserver.core.Commanders;
import com.gserver.core.Packet;
import com.gserver.listener.IClientListener;
import com.gserver.plugins.IPlugin;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2015-2017, James Xiong 熊杰 (xiongjie.cn@gmail.com).
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
 * Created by xiongjie on 2017/10/15.
 */

public abstract class PluginClientSocketListener implements IPlugin {
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap;
    private IClientListener clientListener;
    private ChannelFuture channelFuture;
    private Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
    private Logger logger = Logger.getLogger(this.getClass());
    private ClientConfig config = new ClientConfig();

    protected PluginClientSocketListener() {
        bootstrap = new Bootstrap();
        initConfig(config);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        initOption(optionObjectMap);
        for (Object key : optionObjectMap.keySet()) {
            bootstrap.option((ChannelOption<Object>) key, optionObjectMap.get(key));
        }
        bootstrap.handler(getChannelInitializer());
    }

    public boolean start() {
        try {
            // Start the client.
            channelFuture = bootstrap.connect(config.getHost(), config.getPort()).sync();
            return true;
        } catch (Exception e) {
            logger.error("connect faild", e);
        }
        return false;
    }

    public boolean stop() {
        workerGroup.shutdownGracefully();
        return true;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }


    protected void initConfig(ClientConfig config) {

    }

    protected void initOption(Map<ChannelOption<?>, Object> config) {

    }

    public void setClientListener(IClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public ChannelFuture writeData(byte[] data) {
        if (channelFuture != null) {
            if (channelFuture.channel().isWritable()) {
                ByteBuf bb = Unpooled.buffer();
                bb.order(config.getByteOrder()).writeBytes(data);
                return channelFuture.channel().writeAndFlush(bb);

            } else {
                logger.error("网络不可写");
            }
        } else {
            logger.error("未建立网络连接");
        }
        return null;
    }

    private class GameClientChannelInitializer extends ChannelInitializer<Channel> {
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
            MessageHandler handler = new MessageHandler();
            socketChannel.pipeline().addLast(workerGroup, handler);
        }
    }

    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameClientChannelInitializer();
    }

    private class MessageHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = Logger.getLogger(MessageHandler.class);

        // 接收server端的消息，并打印出来
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                Packet packet = new Packet((Map<String, Object>) msg);
                Commanders.getInstance().dispatch(packet, ctx.channel());
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.error("断开连接" + Calendar.getInstance().getTime().toString());
            PluginClientSocketListener.this.clientListener.onClientDisconnected(ctx);
            super.channelInactive(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            PluginClientSocketListener.this.clientListener.onClientConnected(ctx);
        }

    }
}