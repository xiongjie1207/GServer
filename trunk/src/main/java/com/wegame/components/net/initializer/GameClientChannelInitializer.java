package com.wegame.components.net.initializer;

import com.wegame.components.net.handler.ServerHandler;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {

    public GameClientChannelInitializer() {
    }

    @Override
    protected final void initChannel(Channel socketChannel) throws Exception {
        super.initChannel(socketChannel);
        socketChannel.pipeline().addLast(new ServerHandler());
    }
}