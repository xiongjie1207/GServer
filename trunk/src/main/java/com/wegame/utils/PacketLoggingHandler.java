package com.wegame.utils;

import com.wegame.components.net.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class PacketLoggingHandler extends LoggingHandler {
    public PacketLoggingHandler() {
        super();
    }

    public PacketLoggingHandler(LogLevel level) {
        super(level);
    }

    public PacketLoggingHandler(Class<?> clazz) {
        super(clazz);
    }

    public PacketLoggingHandler(Class<?> clazz, LogLevel level) {
        super(clazz, level);
    }

    public PacketLoggingHandler(String name) {
        super(name);
    }

    public PacketLoggingHandler(String name, LogLevel level) {
        super(name, level);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IPacket packet = (IPacket) msg;
        Loggers.PacketLogger.info("read:"+packet.toString());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        IPacket packet = (IPacket) msg;
        Loggers.PacketLogger.info("write:"+packet.toString());
        ctx.write(msg, promise);
    }
}
