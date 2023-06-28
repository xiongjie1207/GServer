package com.wegame.framework.handler;

import com.wegame.framework.core.GameCons;
import com.wegame.framework.core.GameEventLoop;
import com.wegame.framework.packet.IPacket;
import com.wegame.framework.session.GameSession;
import com.wegame.framework.session.ISession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@ChannelHandler.Sharable
@Slf4j
public class SocketHandler extends SimpleChannelInboundHandler<IPacket> {

    public SocketHandler() {
        super(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception {
        AttributeKey<ISession> key = AttributeKey.valueOf(GameCons.SessionAttrKey);
        ISession session = ctx.channel().attr(key).get();
        GameEventLoop.getInstance().dispatch(msg, session);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ISession session = new GameSession(ctx.channel());
        log.warn("channelActive:" + session);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        AttributeKey<ISession> attributeKey = AttributeKey.valueOf(GameCons.SessionAttrKey);
        if (ctx.channel().hasAttr(attributeKey)) {
            ISession session = ctx.channel().attr(attributeKey).get();
            ctx.channel().attr(attributeKey).set(null);
            log.warn("channelInactive:" + session);

        }
    }
    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE, WRITER_IDLE:
                    break;
                case READER_IDLE:
                    log.info("写空闲:" + ctx.channel());
                    ctx.channel().close();
                    ctx.close();
                    break;
            }

        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException && ctx.channel().isActive()) {
            log.error("simpleclient" + ctx.channel().remoteAddress() + "异常");
        }
        ctx.close();
    }
}