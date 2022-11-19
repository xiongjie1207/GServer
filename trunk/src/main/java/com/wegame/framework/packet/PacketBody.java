package com.wegame.framework.packet;

public class PacketBody {
    private byte[] data = null;

    public PacketBody() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
