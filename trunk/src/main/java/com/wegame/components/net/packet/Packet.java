package com.wegame.components.net.packet;

import com.google.protobuf.Message;
import com.wegame.core.GameCons;
import com.wegame.utils.JsonUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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
public class Packet extends SPacket {
    private Packet(int pid) {
        super(pid);
        if (pid < GameCons.SystemPid) {
            LoggerFactory.getLogger(this.getClass()).warn(String.format("pid必须大于%d!!!%d为系统保留", GameCons.SystemPid, GameCons.SystemPid));
        }

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

    public static Builder<Serializable> newJsonBuilder(int pid) {
        JsonBuilder jsonBuilder = new JsonBuilder(pid);
        return jsonBuilder;
    }

    public static Builder<byte[]> newNetBuilder(int pid) {
        NetBuilder netBuilder = new NetBuilder(pid);
        return netBuilder;
    }

    public static Builder<Message> newProtoBuilder(int pid) {
        ProtoBuilder protoBuilder = new ProtoBuilder(pid);
        return protoBuilder;
    }

    public static class JsonBuilder extends Builder<Serializable> {
        private Serializable data;

        private JsonBuilder(int pid) {
            super(pid);
        }

        @Override
        public JsonBuilder setData(Serializable data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.pid);
            if (this.data != null) {
                packet.packetBody.setData(JsonUtil.toJson(this.data).getBytes());
            }
            return packet;
        }

    }

    public static class NetBuilder extends Builder<byte[]> {
        private byte[] data;

        private NetBuilder(int pid) {
            super(pid);
        }

        @Override
        public NetBuilder setData(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.pid);
            if (this.data != null) {
                packet.packetBody.setData(data);
            }
            return packet;
        }
    }

    public static class ProtoBuilder extends Builder<Message> {
        private Message message;

        private ProtoBuilder(int pid) {
            super(pid);
        }

        @Override
        public ProtoBuilder setData(Message message) {
            this.message = message;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.pid);
            if (this.message != null) {
                packet.packetBody.setData(message.toByteArray());
            }
            return packet;
        }
    }

    public static abstract class Builder<T> {
        protected int pid;

        protected Builder(int pid) {
            this.pid = pid;
        }

        public abstract Builder setData(T message);


        public abstract IPacket build();
    }
}
