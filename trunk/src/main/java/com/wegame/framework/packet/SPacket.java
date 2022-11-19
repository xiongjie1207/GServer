package com.wegame.framework.packet;

public class SPacket implements IPacket {
    protected PacketHead packetHead;
    protected PacketBody packetBody;

    protected SPacket(short module,short id) {
        packetHead = new PacketHead(module,id);
        packetBody = new PacketBody();
    }

    @Override
    public short getModule() {
        return this.packetHead.getModule();
    }

    @Override
    public final short getPid() {
        return this.packetHead.getPid();
    }

    @Override
    public byte[] getData() {
        return this.packetBody.getData();
    }
}
