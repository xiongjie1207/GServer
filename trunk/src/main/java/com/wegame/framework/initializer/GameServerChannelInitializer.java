package com.wegame.framework.initializer;

import com.wegame.framework.handler.ServerSocketHandler;
import io.netty.channel.Channel;

public class GameServerChannelInitializer extends GameChannelInitializer {

    @Override
    protected void initCustomChannel(Channel socketChannel) {
        socketChannel.pipeline().addLast(new ServerSocketHandler());
    }
}