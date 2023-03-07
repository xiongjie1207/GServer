package com.wegame.framework.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerSocketHandler extends ServerHandler {

    public ServerSocketHandler() {
        super();
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
                    LoggerFactory.getLogger(this.getClass()).info("写空闲:" + ctx.channel());
                    ctx.channel().close();
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    break;
            }

        }
    }
}