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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class MessageDecoder extends MessageToMessageDecoder<String> {
    private Logger logger;

    public MessageDecoder() {
        logger = Logger.getLogger(this.getClass());

    }

    @Override
    protected void decode(ChannelHandlerContext paramChannelHandlerContext, String paramI, List<Object> paramList)
            throws Exception {
        // TODO Auto-generated method stub
        try {
            logger.info("socket receive:---------" + paramI);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> json = objectMapper.readValue(paramI, HashMap.class);
            paramList.add(json);
        } catch (Exception e) {
            logger.error(e.getCause(), e);
        }

    }
}