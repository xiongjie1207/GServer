package com.wegame.components.net.listener;

import com.wegame.components.net.initializer.GameClientChannelInitializer;
import com.wegame.config.ClientConfig;
import com.wegame.components.IComponent;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.LoggerFactory;

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

public class ComponentClientSocketListener implements IComponent {
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();

    protected ComponentClientSocketListener() {

    }

    @Override
    public String getName() {
        return "ComponentClientSocketListener";
    }

    @Override
    public boolean start() {
        try {
            bootstrap = new Bootstrap();
            initConfig(ClientConfig.getInstance());
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            initOption(optionObjectMap);
            for (Object key : optionObjectMap.keySet()) {
                bootstrap.option((ChannelOption<Object>) key, optionObjectMap.get(key));
            }
            bootstrap.handler(getChannelInitializer());
            // Start the client.
            channelFuture = bootstrap.connect(ClientConfig.getInstance().getHost(), ClientConfig.getInstance().getPort()).sync();
            channelFuture.addListener((ChannelFutureListener) channelFuture -> OnConnectedHandler(true));
            return true;
        } catch (Exception e) {
            LoggerFactory.getLogger(ComponentClientSocketListener.class).error("connect faild", e);
            OnConnectedHandler(false);
        }
        return false;
    }
    protected void OnConnectedHandler(boolean flag){

    }
    @Override
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

    public ChannelFuture writeData(byte[] data) {
        if (channelFuture != null) {
            if (channelFuture.channel().isWritable()) {
                ByteBuf bb = Unpooled.buffer();
                bb.order(ClientConfig.getInstance().getByteOrder()).writeBytes(data);
                return channelFuture.channel().writeAndFlush(bb);

            } else {
                LoggerFactory.getLogger(ComponentClientSocketListener.class).error("网络不可写");
            }
        } else {
            LoggerFactory.getLogger(ComponentClientSocketListener.class).error("未建立网络连接");
        }
        return null;
    }


    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameClientChannelInitializer();
    }

}