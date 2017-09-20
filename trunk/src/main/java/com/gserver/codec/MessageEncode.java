package com.gserver.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.log4j.Logger;

import java.io.Serializable;
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
public class MessageEncode extends MessageToMessageEncoder<Serializable> {

    private Logger logger;

    public MessageEncode() {
        logger = Logger.getLogger(this.getClass());
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Serializable o, List<Object> list) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(o);
        list.add(jsonData);
    }
}