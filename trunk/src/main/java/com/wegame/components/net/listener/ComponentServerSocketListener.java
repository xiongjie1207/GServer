package com.wegame.components.net.listener;

import com.wegame.components.IComponent;
import com.wegame.components.net.initializer.GameServerChannelInitializer;
import com.wegame.config.ServerConfig;
import com.wegame.utils.Loggers;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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
 * 基于tcp/ip的数据传输
 * 数据流发送格式{"name":"guest","password":"111111","id":1,"clientType":0 }
 * 数据流接收格式{"name":"guest","password":"111111","id":1,"clientType":0 }
 */
public class ComponentServerSocketListener implements IComponent {
    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    private ServerBootstrap serverBootstrap;
    private Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
    private Map<ChannelOption<?>, Object> childOptionObjectMap = new HashMap<>();


    @Override
    public boolean start() {
        runServer();
        return true;
    }

    @Override
    public boolean stop() {
        try {
            channelFuture.channel().close();
            if (bossGroup.shutdownGracefully().await(1, TimeUnit.SECONDS)) {
                Loggers.ServerStatusLogger.info("ServerBoss关闭成功");
            }
            if (workerGroup.shutdownGracefully().await(1, TimeUnit.SECONDS)) {
                Loggers.ServerStatusLogger.info("ServerWork关闭成功");
            }
        } catch (Exception e) {
            Loggers.ErrorLogger.error("关闭网络时发生异常:", e);
        }

        return true;
    }

    @Override
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
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(getChannelInitializer());
            initOption(optionObjectMap);
            for (Object key : optionObjectMap.keySet()) {
                serverBootstrap.option((ChannelOption<Object>) key, optionObjectMap.get(key));
            }
            initChildOption(childOptionObjectMap);
            for (Object key : childOptionObjectMap.keySet()) {
                serverBootstrap.childOption((ChannelOption<Object>) key, childOptionObjectMap.get(key));
            }
            serverBootstrap.localAddress(new InetSocketAddress(ServerConfig.getInstance().getPort()));

            channelFuture = serverBootstrap.bind().addListener((ChannelFuture future) -> this.operationComplete(future));
            channelFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            Loggers.ErrorLogger.error("", e);
        }
    }

    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private void operationComplete(ChannelFuture future) throws Exception {
        // TODO Auto-generated method stub
        if (future.isSuccess()) {
            Loggers.ServerStatusLogger.info("[GServer] Started success");
        } else {
            Loggers.ServerStatusLogger.error("[GServer] Started Failed");
        }
    }

    protected void initConfig(ServerConfig config) {

    }

    protected void initOption(Map<ChannelOption<?>, Object> config) {
    }

    protected void initChildOption(Map<ChannelOption<?>, Object> config) {
    }


}