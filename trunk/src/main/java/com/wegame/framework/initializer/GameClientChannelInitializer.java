package com.wegame.framework.initializer;

import com.wegame.framework.component.net.AbsTcpClientComponent;
import com.wegame.framework.handler.ClientSocketHandler;
import io.netty.channel.Channel;

public class GameClientChannelInitializer extends GameChannelInitializer {
    private AbsTcpClientComponent clientSocketPlugin;
    public GameClientChannelInitializer(AbsTcpClientComponent clientSocketPlugin) {
        this.clientSocketPlugin = clientSocketPlugin;
    }
    @Override
    protected void initCustomChannel(Channel socketChannel) {
        socketChannel.pipeline().addLast(new ClientSocketHandler(this.clientSocketPlugin));
    }
}