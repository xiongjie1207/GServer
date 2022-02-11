package com.wegame.components.net.handler;

import com.wegame.components.net.packet.IPacket;
import com.wegame.components.net.packet.Packet;
import com.wegame.components.session.GameSession;
import com.wegame.components.session.ISession;
import com.wegame.components.session.SessionCounter;
import com.wegame.config.SProtocol;
import com.wegame.config.ServerConfig;
import com.wegame.utils.Loggers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class SocketServerHandler extends ServerHandler {

    public SocketServerHandler() {
        super();
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ISession session = new GameSession(ctx.channel());
        long count = SessionCounter.getInstance().incrementAndGet();
        if (ServerConfig.getInstance().getMaxOnlinePlayer() < count) {
            IPacket packet = Packet.newProtoBuilder(SProtocol.SessionCountOutOfRange).build();
            session.write(packet).addListener((future) -> {
                session.close();
                ctx.close();
            });

        }
    }

    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE:
                    break;
                case READER_IDLE:
                    Loggers.GameLogger.info("写空闲:" + ctx.channel());
                    ctx.channel().close();
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    break;
            }

        }
    }
}