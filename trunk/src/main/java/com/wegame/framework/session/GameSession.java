package com.wegame.framework.session;

import com.wegame.framework.core.GameCons;
import com.wegame.framework.packet.IPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import java.lang.ref.WeakReference;

public class GameSession<T> implements ChannelFutureListener, ISession<T> {
    private WeakReference<Channel> channel;
    private ChannelFutureListener onCloseOperationComplete;
    private T dataOjbect;

    public GameSession(Channel channel) {
        this.channel = new WeakReference<>(channel);
        AttributeKey<ISession> attributeKey = AttributeKey.valueOf(GameCons.SessionAttrKey);
        this.channel.get().attr(attributeKey).set(this);
        this.channel.get().closeFuture().addListener(this);
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
        if (channel.get() != null) {
            return channel.get().isActive();
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

    public void setChannel(Channel channel) {
        this.channel.refersTo(channel);
    }

    @Override
    public ChannelFuture write(IPacket msg) {
        Channel channel1 = channel.get();
        if(channel1!=null&&channel1.isWritable()){
            return channel1.writeAndFlush(msg);
        }
        return null;

    }

    @Override
    public ChannelFuture close() {
        if(channel.get()!=null){
            return channel.get().close();
        }
        return null;

    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (this.onCloseOperationComplete != null) {
            this.onCloseOperationComplete.operationComplete(future);
        }

    }

    @Override
    public String toString() {
        if(channel.get()!=null){
            return String.format("session channelId is:%s,remote address is:%s",
                channel.get().id().asLongText(), channel.get().remoteAddress());
        }
        return String.valueOf(this.hashCode());
    }
}
