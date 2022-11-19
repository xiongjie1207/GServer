package com.wegame.framework.initializer;

import com.wegame.framework.codec.MessageDecoder;
import com.wegame.framework.codec.MessageEncode;
import com.wegame.framework.config.ServerConfig;
import com.wegame.framework.core.SpringContext;
import com.wegame.utils.PacketLoggingHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

public abstract class GameChannelInitializer extends ChannelInitializer<Channel> {
    @Override
    protected final void initChannel(Channel socketChannel) {
        // TODO Auto-generated method stub
        //接收数据通道
        //指定数据长度
        socketChannel.pipeline().addLast(LengthFieldBasedFrameDecoder.class.getSimpleName(),
            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        //解码成java对象
        socketChannel.pipeline()
            .addLast(MessageDecoder.class.getSimpleName(), new MessageDecoder());
        //////////////////////////////////////////////////////////////////////////////////
        //发送数据通道
        //添加数据长度
        socketChannel.pipeline()
            .addLast(LengthFieldPrepender.class.getSimpleName(), new LengthFieldPrepender(4));
        //对数据压缩
        //对java对象编码
        socketChannel.pipeline().addLast(MessageEncode.class.getSimpleName(), new MessageEncode());
        ServerConfig serverConfig = SpringContext.getBean(ServerConfig.class);
        IdleStateHandler idleStateHandler =
            new IdleStateHandler(serverConfig.getReaderIdleTimeSeconds(),
                serverConfig.getWriterIdleTimeSeconds(),
                serverConfig.getAllIdleTimeSeconds(), TimeUnit.SECONDS);
        socketChannel.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
        socketChannel.pipeline().addLast(PacketLoggingHandler.class.getSimpleName(),
            new PacketLoggingHandler(LogLevel.INFO));
        initCustomChannel(socketChannel);

    }
    protected abstract void initCustomChannel(Channel socketChannel);
}