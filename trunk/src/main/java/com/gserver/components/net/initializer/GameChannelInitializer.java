package com.gserver.components.net.initializer;

import com.gserver.codec.MessageDecoder;
import com.gserver.codec.MessageEncode;
import com.gserver.config.ServerConfig;
import com.gserver.utils.PacketLoggingHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class GameChannelInitializer extends ChannelInitializer<Channel> {

    public GameChannelInitializer() {
    }

    @Override
    protected void initChannel(Channel socketChannel) throws Exception {
        // TODO Auto-generated method stub
        //接收数据通道
        //指定数据长度
        socketChannel.pipeline().addLast(LengthFieldBasedFrameDecoder.class.getSimpleName(),new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        //解码成java对象
        socketChannel.pipeline().addLast(MessageDecoder.class.getSimpleName(), new MessageDecoder());
        //////////////////////////////////////////////////////////////////////////////////
        //发送数据通道
        //添加数据长度
        socketChannel.pipeline().addLast(LengthFieldPrepender.class.getSimpleName(), new LengthFieldPrepender(4));
        //对数据压缩
        //对java对象编码
        socketChannel.pipeline().addLast(MessageEncode.class.getSimpleName(), new MessageEncode());
        IdleStateHandler idleStateHandler = new IdleStateHandler(ServerConfig.getInstance().getReaderIdleTimeSeconds(), ServerConfig.getInstance().getWriterIdleTimeSeconds(), ServerConfig.getInstance().getAllIdleTimeSeconds(), TimeUnit.SECONDS);
        socketChannel.pipeline().addLast(IdleStateHandler.class.getSimpleName(), idleStateHandler);
        socketChannel.pipeline().addLast(PacketLoggingHandler.class.getSimpleName(), new PacketLoggingHandler(LogLevel.DEBUG));
    }
}