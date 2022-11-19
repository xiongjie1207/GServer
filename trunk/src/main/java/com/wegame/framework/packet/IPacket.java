package com.wegame.framework.packet;

/**
 * @author xiongjie
 */
public interface IPacket {
    short getModule();
    short getPid();

    byte[] getData();
}
