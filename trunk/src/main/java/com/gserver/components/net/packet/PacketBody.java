package com.gserver.components.net.packet;

public class PacketBody {
    public PacketBody() {
    }

    private byte[] data = null;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
