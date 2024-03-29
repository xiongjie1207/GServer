package com.wegame.framework.packet;


public class Packet implements IPacket {
    protected PacketHead packetHead;
    protected PacketBody packetBody;

    public Packet(short module, int id) {
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
        return packetBody.getData();
    }

    public void setData(byte[] data) {
        this.packetBody.setData(data);
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("module:").append(this.getModule()).append("/");
        stringBuilder.append("pid:").append(this.getPid());
        return stringBuilder.toString();
    }
}
