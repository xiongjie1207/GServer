package com.wegame.framework.packet;

import lombok.Data;

@Data
public class PacketHead {
    private short pid;
    private short module;
    public PacketHead(short module,short pid)
    {
        this.module = module;
        this.pid = pid;
    }


}
