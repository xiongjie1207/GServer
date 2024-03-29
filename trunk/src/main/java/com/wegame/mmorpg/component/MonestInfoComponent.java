package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class MonestInfoComponent {
    /**
     * 怪物类型
     */
    @Protobuf(order = 1, required = true)
    private int type;
    /**
     * 怪物的Hp
     */
    @Protobuf(order = 2, required = true)
    private int hp;
    /**
     * 怪物最大血量
     */
    @Protobuf(order = 3, required = true)
    private int maxHp;
    /**
     * 怪物的攻击力
     */
    @Protobuf(order = 4, required = true)
    private int attack;
    /**
     * 怪物的防御力
     */
    @Protobuf(order = 5, required = true)
    private int def;
    /**
     * 掉经验值
     */
    @Protobuf(order = 6, required = true)
    private int exp;
    /**
     * 移动速度
     */
    @Protobuf(order = 7, required = true)
    private float speed;

}
