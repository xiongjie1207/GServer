package com.gserver.components.net.listener;

import com.gserver.components.net.handler.WebSocketServerHandler;
import com.gserver.config.ServerConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public abstract class ComponentWebSocketListener extends ComponentServerSocketListener {
    @Override
    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private class GameServerChannelInitializer extends ChannelInitializer<Channel> {

        public GameServerChannelInitializer() {
        }

        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast("http-decorder", new HttpRequestDecoder());
            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
            IdleStateHandler idleStateHandler = new IdleStateHandler(ServerConfig.getInstance().getReaderIdleTimeSeconds(), ServerConfig.getInstance().getWriterIdleTimeSeconds(), ServerConfig.getInstance().getAllIdleTimeSeconds(), TimeUnit.SECONDS);
            ch.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
            WebSocketServerHandler handler = new WebSocketServerHandler();
            ch.pipeline().addLast(handler);
        }

    }


}
