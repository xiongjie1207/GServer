package com.gserver.components.net.handler;

import com.gserver.components.net.packet.IPacket;
import com.gserver.components.net.packet.Packet;
import com.gserver.components.session.GameSession;
import com.gserver.components.session.ISession;
import com.gserver.components.session.SessionCounter;
import com.gserver.config.SProtocol;
import com.gserver.config.ServerConfig;
import com.gserver.utils.Loggers;
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