package com.wegame.framework.handler;

import com.wegame.framework.plugin.net.AbsClientSocketPlugin;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@ChannelHandler.Sharable
public class ClientSocketHandler extends ServerHandler {
    private AbsClientSocketPlugin clientSocketPlugin;
    public ClientSocketHandler(AbsClientSocketPlugin clientSocketPlugin) {
        this.clientSocketPlugin = clientSocketPlugin;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE:
                    break;
                case READER_IDLE:
                    log.info("写空闲:" + ctx.channel());
                    ctx.channel().close();
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    break;
            }

        }
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.err.println("运行中断开重连。。。");
        clientSocketPlugin.connect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException && ctx.channel().isActive()) {
            log.error("client socket" + ctx.channel().remoteAddress() + "异常");
        }
        ctx.close();
    }
}