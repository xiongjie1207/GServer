package com.wegame.framework.initializer;

import com.wegame.framework.handler.ServerHandler;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {

    @Override
    protected void initCustomChannel(Channel socketChannel) {
        socketChannel.pipeline().addLast(new ServerHandler());
    }
}