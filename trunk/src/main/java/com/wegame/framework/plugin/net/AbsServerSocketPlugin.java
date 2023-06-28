package com.wegame.framework.plugin.net;

import com.wegame.framework.config.ServerConfig;
import com.wegame.framework.core.SpringContext;
import com.wegame.framework.initializer.GameServerChannelInitializer;
import com.wegame.framework.plugin.IPlugin;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

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
 * 基于tcp/ip的数据传输
 * |-4字节长度-|-2字节module-|-2字节pid-|--数据--|
 */
@Slf4j
public abstract class AbsServerSocketPlugin implements IPlugin {
    private final Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
    private final Map<ChannelOption<?>, Object> childOptionObjectMap = new HashMap<>();
    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    private ServerBootstrap serverBootstrap;

    @Override
    public final boolean start() {
        try {
            serverBootstrap = new ServerBootstrap();
            ServerConfig serverConfig = SpringContext.getBean(ServerConfig.class);
            initConfig(serverConfig);
            if (serverConfig.getBossCount() == 0) {
                bossGroup = new NioEventLoopGroup();
            } else {
                bossGroup = new NioEventLoopGroup(serverConfig.getBossCount());
            }
            if (serverConfig.getWorkerCount() == 0) {
                workerGroup = new NioEventLoopGroup();
            } else {
                workerGroup = new NioEventLoopGroup(serverConfig.getWorkerCount());
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
                serverBootstrap.childOption((ChannelOption<Object>) key,
                    childOptionObjectMap.get(key));
            }
            serverBootstrap.localAddress(
                new InetSocketAddress(serverConfig.getPort()));

            channelFuture = serverBootstrap.bind()
                .addListener((ChannelFuture future) -> this.operationComplete(future));
            channelFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
        } catch (Exception e) {
            log.error("", e);
        }
        return true;
    }

    @Override
    public final boolean stop() {
        try {
            channelFuture.channel().close();
            if (bossGroup.shutdownGracefully().await(1, TimeUnit.SECONDS)) {
                log.info("ServerBoss关闭成功");
            }
            if (workerGroup.shutdownGracefully().await(1, TimeUnit.SECONDS)) {
                log.info("ServerWork关闭成功");
            }
        } catch (Exception e) {
            log.error("关闭网络时发生异常:", e);
        }

        return true;
    }

    @Override
    public String getName() {
        return "ComponentServerSocketListener";
    }


    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private void operationComplete(ChannelFuture future) {
        // TODO Auto-generated method stub
        if (future.isSuccess()) {
            log.info("[GServer] Started success");
        } else {
            log.error("[GServer] Started Failed");
        }
    }

    protected void initConfig(ServerConfig config) {

    }

    protected void initOption(Map<ChannelOption<?>, Object> config) {
    }

    protected void initChildOption(Map<ChannelOption<?>, Object> config) {
    }


}