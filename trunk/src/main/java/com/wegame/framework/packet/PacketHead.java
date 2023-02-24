package com.wegame.framework.packet;

import lombok.Data;

@Data
public class PacketHead {
    private int pid;
    private short module;
    public PacketHead(short module,int pid)
    {
        this.module = module;
        this.pid = pid;
    }


}
