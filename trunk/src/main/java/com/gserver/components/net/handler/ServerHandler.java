package com.gserver.components.net.handler;

import com.gserver.components.session.GameSession;
import com.gserver.components.session.ISession;
import com.gserver.components.session.SessionCounter;
import com.gserver.core.CommanderGroup;
import com.gserver.core.GameCons;
import com.gserver.components.net.packet.IPacket;
import com.gserver.utils.Loggers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.io.IOException;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<IPacket> {

    public ServerHandler() {
        super(true);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception {
        AttributeKey<ISession> key = AttributeKey.valueOf(GameCons.SessionAttrKey);
        ISession session = ctx.channel().attr(key).get();
        CommanderGroup.getInstance().dispatch(msg, session);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ISession session = new GameSession(ctx.channel());
        Loggers.SessionLogger.warn("channelRegistered:"+session);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        AttributeKey<ISession> attributeKey = AttributeKey.valueOf(GameCons.SessionAttrKey);
        if (ctx.channel().hasAttr(attributeKey)) {
            ISession session = ctx.channel().attr(attributeKey).get();
            ctx.channel().attr(attributeKey).set(null);
            Loggers.SessionLogger.warn("channelUnregistered:"+session);

        }
        SessionCounter.getInstance().decrementAndGet();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException && ctx.channel().isActive()) {
            Loggers.SessionLogger.error("simpleclient" + ctx.channel().remoteAddress() + "异常");
        }
        ctx.close();
    }
}