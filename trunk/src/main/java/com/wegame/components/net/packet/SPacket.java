package com.wegame.components.net.packet;

public class SPacket implements IPacket {
    protected PacketHead packetHead;
    protected PacketBody packetBody;

    protected SPacket(int id) {
        packetHead = new PacketHead(id);
        packetBody = new PacketBody();
    }

    @Override
    public final int getPid() {
        return this.packetHead.getPid();
    }

    @Override
    public byte[] getData() {
        return this.packetBody.getData();
    }
}
