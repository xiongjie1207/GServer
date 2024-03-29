package com.wegame.framework.component.net;

import com.wegame.framework.component.IComponent;
import com.wegame.framework.config.ClientConfig;
import com.wegame.framework.initializer.GameClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
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
@Slf4j
public abstract class AbsTcpClientComponent implements IComponent {
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Map<ChannelOption<?>, Object> optionObjectMap = new HashMap<>();
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private ClientConfig config;

    protected AbsTcpClientComponent() {

    }

    @Override
    public String getName() {
        return "ComponentClientSocketListener";
    }

    @Override
    public final boolean start() {
        bootstrap = new Bootstrap();
        config = new ClientConfig();
        initConfig(config);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        initOption(optionObjectMap);
        for (Object key : optionObjectMap.keySet()) {
            bootstrap.option((ChannelOption<Object>) key, optionObjectMap.get(key));
        }
        bootstrap.handler(getChannelInitializer());
        this.connect();
        return true;
    }

    @Override
    public boolean stop() {
        workerGroup.shutdownGracefully();
        return true;
    }
    public ClientConfig getConfig(){
        return this.config;
    }
    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }


    protected abstract void initConfig(ClientConfig config);

    protected void initOption(Map<ChannelOption<?>, Object> config) {

    }

    public ChannelFuture writeData(byte[] data) {
        if (channelFuture != null) {
            if (channelFuture.channel().isWritable()) {
                ByteBuf bb = Unpooled.buffer();
                bb.order(config.getByteOrder()).writeBytes(data);
                return channelFuture.channel().writeAndFlush(bb);

            } else {
                log.error("网络不可写");
            }
        } else {
            log.error("未建立网络连接");
        }
        return null;
    }


    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameClientChannelInitializer(this);
    }

    public void connect(){
        log.debug(MessageFormat.format("正在连接服务器...{0}:{1}",this.config.getHost(),String.valueOf(this.config.getPort())));
        channelFuture = bootstrap.connect(this.config.getHost(),this.config.getPort());
    }
    public void close(){
        if(channelFuture!=null){
            channelFuture.channel().close();
        }
    }
}