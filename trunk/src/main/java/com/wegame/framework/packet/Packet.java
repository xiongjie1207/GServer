package com.wegame.framework.packet;

import com.wegame.util.JsonUtils;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

public class Packet implements IPacket {
    protected PacketHead packetHead;
    protected PacketBody packetBody;

    protected Packet(short module, int id) {
        packetHead = new PacketHead(module, id);
        packetBody = new PacketBody();
    }

    @Override
    public short getModule() {
        return this.packetHead.getModule();
    }

    @Override
    public final int getPid() {
        return this.packetHead.getPid();
    }

    @Override
    public byte[] getData() {
        return this.packetBody.getData();
    }

    public static Packet.Builder<Serializable> newJsonBuilder(short module, short pid) {
        Packet.JsonBuilder jsonBuilder = new Packet.JsonBuilder(module, pid);
        return jsonBuilder;
    }

    public static Packet.Builder<byte[]> newByteBuilder(short module, int pid) {
        Packet.NetBuilder netBuilder = new Packet.NetBuilder(module, pid);
        return netBuilder;
    }


    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("协议:").append(this.getModule()).append("/").append(this.getPid());
        if (this.getData() != null) {
            String body = new String(this.getData(), CharsetUtil.UTF_8);
            msg.append("----").append("数据:").append(body);
        }
        return msg.toString();
    }

    public static class JsonBuilder extends Packet.Builder<Serializable> {
        private Serializable data;

        private JsonBuilder(short module, short pid) {
            super(module, pid);
        }

        @Override
        public Packet.JsonBuilder setData(Serializable data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.module, this.pid);
            if (this.data != null) {
                packet.packetBody.setData(JsonUtils.toJson(this.data).getBytes());
            }
            return packet;
        }

    }


    public static class NetBuilder extends Packet.Builder<byte[]> {
        private byte[] data;

        private NetBuilder(short module, int pid) {
            super(module, pid);
        }

        @Override
        public Packet.NetBuilder setData(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public IPacket build() {
            Packet packet = new Packet(this.module, this.pid);
            if (this.data != null) {
                packet.packetBody.setData(data);
            }
            return packet;
        }
    }

    public static abstract class Builder<T> {
        protected int pid;
        protected short module;

        protected Builder(short module, int pid) {
            this.module = module;
            this.pid = pid;
        }

        public abstract Packet.Builder setData(T message);


        public abstract IPacket build();
    }
}
