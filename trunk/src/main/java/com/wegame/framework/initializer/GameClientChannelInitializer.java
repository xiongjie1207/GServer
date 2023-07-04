package com.wegame.framework.initializer;

import com.wegame.framework.handler.ClientSocketHandler;
import com.wegame.framework.plugin.net.AbsClientSocketComponent;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {
    private AbsClientSocketComponent clientSocketPlugin;
    public GameClientChannelInitializer(AbsClientSocketComponent clientSocketPlugin) {
        this.clientSocketPlugin = clientSocketPlugin;
    }
    @Override
    protected void initCustomChannel(Channel socketChannel) {
        socketChannel.pipeline().addLast(new ClientSocketHandler(this.clientSocketPlugin));
    }
}