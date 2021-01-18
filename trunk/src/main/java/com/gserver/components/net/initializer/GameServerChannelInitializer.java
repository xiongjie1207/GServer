package com.gserver.components.net.initializer;

import com.gserver.components.net.handler.SocketServerHandler;
import io.netty.channel.Channel;

public class GameServerChannelInitializer extends GameChannelInitializer {

    public GameServerChannelInitializer() {
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        super.initChannel(socketChannel);
        socketChannel.pipeline().addLast(new SocketServerHandler());
    }
}