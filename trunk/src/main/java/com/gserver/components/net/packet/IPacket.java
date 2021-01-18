package com.gserver.components.net.packet;

import javafx.scene.input.DataFormat;

/**
 * @author xiongjie
 */
public interface IPacket {

    int getPid();

    byte[] getData();
}
