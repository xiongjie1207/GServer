package com.wegame.framework.codec;
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

import com.wegame.framework.packet.IPacket;
import com.wegame.framework.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.nio.charset.Charset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    protected void decode(ChannelHandlerContext paramChannelHandlerContext, ByteBuf bytebuf,
                          List<Object> paramList) {
        // TODO Auto-generated method stub
        try {
            short module = bytebuf.readShort();
            short pid = bytebuf.readShort();
            Packet.Builder builder = Packet.newNetBuilder(module,pid);
            int byteLength = bytebuf.readableBytes();
            if (byteLength > 0) {
                byte[] bytes = new byte[byteLength];
                bytebuf.getBytes(bytebuf.readerIndex(), bytes);
                builder.setData(bytes);
            }
            IPacket packet = builder.build();
            paramList.add(packet);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}