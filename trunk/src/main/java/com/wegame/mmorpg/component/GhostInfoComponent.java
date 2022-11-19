package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class GhostInfoComponent {
    @Protobuf(order = 1, required = true)
    private int level;

    @Protobuf(order = 2, required = true)
    private int job;

    @Protobuf(order = 3, required = true)
    private float speed;

}
