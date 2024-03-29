package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class PlayerInfoComponent {
    @Protobuf(order = 1, required = true)
    private int exp;

    @Protobuf(order = 2, required = true)
    private int hp;

    @Protobuf(order = 3, required = true)
    private int mp;

    @Protobuf(order = 4, required = true)
    private int coin;

    @Protobuf(order = 5, required = true)
    private int attack;

    @Protobuf(order = 6, required = true)
    private int def;

    @Protobuf(order = 7, required = true)
    private int maxHp;

    @Protobuf(order = 8, required = true)
    private int maxMp;

    @Protobuf(order = 9, required = true)
    private float speed;

}
