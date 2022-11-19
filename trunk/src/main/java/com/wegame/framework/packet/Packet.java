package com.wegame.framework.packet;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.wegame.utils.JsonUtil;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.io.Serializable;
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
 * Created by xiongjie on 2016/12/22.
 */
@Slf4j
public class Packet extends SPacket {
    private Packet(short module,short pid) {
        super(module,pid);
    }

    public static Builder<Serializable> newJsonBuilder(short module,short pid) {
        JsonBuilder jsonBuilder = new JsonBuilder(module,pid);
        return jsonBuilder;
    }
    public static Builder<Serializable> newJsonBuilder(short pid) {
        return newJsonBuilder((short) 0,pid);
    }
    public static Builder<byte[]> newNetBuilder(short module,short pid) {
        NetBuilder netBuilder = new NetBuilder(module,pid);
        return netBuilder;
    }
    public static Builder<byte[]> newNetBuilder(short pid) {
        return newNetBuilder((short) 0,pid);
    }
    public static ProtoBuilder newProtoBuilder(short module,short pid) {
        ProtoBuilder protoBuilder = new ProtoBuilder(module,pid);
        return protoBuilder;
    }
    public static ProtoBuilder newProtoBuilder(short pid) {
        return newProtoBuilder((short) 0,pid);
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("协议id:").append(this.getPid());
        if (this.getData() != null) {
            String body = new String(this.getData(), CharsetUtil.UTF_8);
            msg.append("----").append("数据:").append(body);
        }
        return msg.toString();
    }

    public static class JsonBuilder extends Builder<Serializable> {
        private Serializable data;

        private JsonBuilder(short module,short pid) {
            super(module,pid);
        }

        @Override
        public JsonBuilder setData(Serializable data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.module,this.pid);
            if (this.data != null) {
                packet.packetBody.setData(JsonUtil.toJson(this.data).getBytes());
            }
            return packet;
        }

    }

    public static class NetBuilder extends Builder<byte[]> {
        private byte[] data;

        private NetBuilder(short module,short pid) {
            super(module,pid);
        }

        @Override
        public NetBuilder setData(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.module,this.pid);
            if (this.data != null) {
                packet.packetBody.setData(data);
            }
            return packet;
        }
    }

    public static class ProtoBuilder {
        private short module;
        private short pid;
        private byte[] data;

        private ProtoBuilder(short module,short pid) {
            this.module = module;
            this.pid = pid;
        }

        public <T> ProtoBuilder setData(T object) {
            if(object!=null){
                try {
                    Class clazz = object.getClass();
                    Codec<T> codec = ProtobufProxy.create(clazz);
                    data = codec.encode(object);
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
            return this;
        }

        public IPacket build() {
            Packet packet = new Packet(this.module,this.pid);
            if (this.data != null) {
                packet.packetBody.setData(data);
            }
            return packet;
        }
    }

    public static abstract class Builder<T> {
        protected short pid;
        protected short module;

        protected Builder(short module,short pid) {
            this.module = module;
            this.pid = pid;
        }

        public abstract Builder setData(T message);


        public abstract IPacket build();
    }
}
