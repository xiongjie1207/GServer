package com.wegame.components.net.packet;

/**
 * @author xiongjie
 */
public interface IPacket {

    int getPid();

    byte[] getData();
}
