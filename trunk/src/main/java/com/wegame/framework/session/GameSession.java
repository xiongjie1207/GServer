package com.wegame.framework.session;

import com.wegame.framework.core.GameCons;
import com.wegame.framework.packet.IPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

public class GameSession<T> implements ChannelFutureListener, ISession<T> {
    private Channel channel;
    private ChannelFutureListener onCloseOperationComplete;
    private T dataOjbect;

    public GameSession(Channel channel) {
        this.channel = channel;
        AttributeKey<ISession> attributeKey = AttributeKey.valueOf(GameCons.SessionAttrKey);
        this.channel.attr(attributeKey).set(this);
        this.channel.closeFuture().addListener(this);
    }

    public void setOnCloseOperationComplete(ChannelFutureListener onCloseOperationComplete) {
        this.onCloseOperationComplete = onCloseOperationComplete;
    }

    @Override
    public T getObject() {
        return dataOjbect;
    }

    @Override
    public void setOjbect(T object) {
        this.dataOjbect = object;
    }

    @Override
    public boolean isConnected() {
        if (channel != null) {
            return channel.isActive();
        }
        return false;
    }

    @Override
    public ChannelFuture writeAndDisconnect(IPacket msg) {
        ChannelFuture cf = write(msg);
        if (cf != null) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
        return cf;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public ChannelFuture write(IPacket msg) {
        return channel.writeAndFlush(msg);
    }

    @Override
    public ChannelFuture close() {
        return channel.close();
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (this.onCloseOperationComplete != null) {
            this.onCloseOperationComplete.operationComplete(future);
        }

    }

    @Override
    public String toString() {
        return String.format("session channelId is:%s,remote address is:%s",
            channel.id().asLongText(), channel.remoteAddress());
    }
}
