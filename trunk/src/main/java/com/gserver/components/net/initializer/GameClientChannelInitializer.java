package com.gserver.components.net.initializer;

import com.gserver.components.net.handler.ServerHandler;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {

    public GameClientChannelInitializer() {
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        super.initChannel(socketChannel);
        socketChannel.pipeline().addLast(new ServerHandler());
    }
}