package com.wegame.framework.initializer;

import com.wegame.framework.handler.ClientSocketHandler;
import com.wegame.framework.plugin.net.AbsClientSocketPlugin;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {
    private AbsClientSocketPlugin clientSocketPlugin;
    public GameClientChannelInitializer(AbsClientSocketPlugin clientSocketPlugin) {
        this.clientSocketPlugin = clientSocketPlugin;
    }
    @Override
    protected void initCustomChannel(Channel socketChannel) {
        socketChannel.pipeline().addLast(new ClientSocketHandler(this.clientSocketPlugin));
    }
}