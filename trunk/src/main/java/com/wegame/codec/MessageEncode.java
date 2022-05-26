package com.wegame.codec;

import com.wegame.components.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

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
@ChannelHandler.Sharable
public class MessageEncode extends MessageToMessageEncoder<IPacket> {
    private final Charset charset;

    public MessageEncode() {
        this(Charset.defaultCharset());
    }

    public MessageEncode(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacket packet, List<Object> list) throws Exception {
        try {
            ByteBuf byteBuf = Unpooled.buffer(256);
            byteBuf.writeInt(packet.getPid());
            if (packet.getData() != null) {
                byteBuf.writeBytes(packet.getData());
            }
            list.add(byteBuf);
            LoggerFactory.getLogger(this.getClass()).debug(packet.toString());
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }

    }
}