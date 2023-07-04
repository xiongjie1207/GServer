package com.wegame.framework.handler;

import com.wegame.framework.plugin.net.AbsClientSocketComponent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
@Slf4j
@ChannelHandler.Sharable
public class ClientSocketHandler extends SocketHandler {
    private AbsClientSocketComponent clientSocketPlugin;
    public ClientSocketHandler(AbsClientSocketComponent clientSocketPlugin) {
        this.clientSocketPlugin = clientSocketPlugin;
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if(clientSocketPlugin.getConfig().isReconnect()){
            try {
                log.error("连接失败,开始重连...");
                TimeUnit.SECONDS.sleep(1);
                clientSocketPlugin.connect();
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }
}