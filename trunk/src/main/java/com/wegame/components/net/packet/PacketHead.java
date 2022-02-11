package com.wegame.components.net.packet;


public class PacketHead {
    private int pid;
    public PacketHead(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

}
