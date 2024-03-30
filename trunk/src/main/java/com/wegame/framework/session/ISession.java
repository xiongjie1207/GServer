package com.wegame.framework.session;

import com.wegame.framework.packet.IPacket;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public interface ISession {
    boolean isConnected();

    ChannelFuture write(IPacket msg);

    ChannelFuture writeAndDisconnect(IPacket msg);

    ChannelFuture close();

    Object getObject();

    void setOjbect(Object ojbect);

    void addOnCloseOperationComplete(ChannelFutureListener onCloseOperationComplete);
}
