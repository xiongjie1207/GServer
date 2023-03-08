package com.wegame.util;

import com.wegame.framework.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PacketLoggingHandler extends LoggingHandler {
    public PacketLoggingHandler(LogLevel level) {
        super(level);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        IPacket packet = (IPacket) msg;
        log.debug("read:" + packet.toString());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        IPacket packet = (IPacket) msg;
        log.debug("write:" + packet.toString());
        ctx.write(msg, promise);
    }
}
