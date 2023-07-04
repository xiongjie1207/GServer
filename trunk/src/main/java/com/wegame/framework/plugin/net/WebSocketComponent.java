package com.wegame.framework.plugin.net;

import com.wegame.framework.config.ServerConfig;
import com.wegame.framework.core.SpringContext;
import com.wegame.framework.handler.WebServerSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketComponent extends AbsServerSocketComponent {

    @Override
    protected ChannelInitializer<Channel> getChannelInitializer() {
        return new GameServerChannelInitializer();
    }

    private class GameServerChannelInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast("http-decorder", new HttpRequestDecoder());
            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
            ServerConfig config = SpringContext.getBean(ServerConfig.class);
            IdleStateHandler idleStateHandler =
                new IdleStateHandler(config.getReaderIdleTimeSeconds(),
                    config.getWriterIdleTimeSeconds(),
                    config.getAllIdleTimeSeconds(), TimeUnit.SECONDS);
            ch.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
            WebServerSocketHandler handler = new WebServerSocketHandler();
            ch.pipeline().addLast(handler);
        }

    }


}
