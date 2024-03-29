package com.wegame.framework.packet;

/**
 * @author xiongjie
 */
public interface IPacket {
    short getModule();

    int getPid();

    byte[] getData();

}
