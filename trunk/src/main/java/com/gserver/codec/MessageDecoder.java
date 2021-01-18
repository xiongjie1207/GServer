package com.gserver.codec;
/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2016/12/22.
 */

import com.gserver.components.net.packet.Packet;
import com.gserver.utils.Loggers;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Charset charset;
    public MessageDecoder() {
        this(Charset.defaultCharset());
    }
    public MessageDecoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    @Override
    protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf bytebuf, List<Object> paramList)
            throws Exception {
        // TODO Auto-generated method stub
        try {
            int pid = bytebuf.readInt();
            Packet.Builder builder = Packet.newNetBuilder(pid);
            int byteLength = bytebuf.readableBytes();
            if(byteLength>0){
                byte[] bytes = new byte[byteLength];
                bytebuf.getBytes(bytebuf.readerIndex(), bytes);
                builder.setData(bytes);
            }
            paramList.add(builder.build());
        } catch (Exception e) {
            Loggers.ErrorLogger.error(e.getMessage(), e);
        }

    }
}