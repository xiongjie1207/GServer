package com.wegame.framework.codec;

import com.wegame.framework.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.nio.charset.Charset;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
 * Created by xiongjie on 2017/5/15.
 */
@Slf4j
@Data
@ChannelHandler.Sharable
public class MessageEncode extends MessageToMessageEncoder<IPacket> {
    public MessageEncode() {
        this(Charset.defaultCharset());
    }

    public MessageEncode(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacket packet,
                          List<Object> list) {
        try {
            ByteBuf byteBuf = Unpooled.buffer(256);
            byteBuf.writeShort(packet.getModule());
            byteBuf.writeShort(packet.getPid());
            if (packet.getData() != null) {
                byteBuf.writeBytes(packet.getData());
            }
            list.add(byteBuf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}