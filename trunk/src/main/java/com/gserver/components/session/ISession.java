package com.gserver.components.session;

import com.gserver.components.net.packet.IPacket;
import io.netty.channel.ChannelFuture;

public interface ISession<T> {
    boolean isConnected();

    ChannelFuture write(IPacket msg);

    ChannelFuture writeAndDisconnect(IPacket msg);

    ChannelFuture close();

    T getObject();

    void setOjbect(T ojbect);
}
