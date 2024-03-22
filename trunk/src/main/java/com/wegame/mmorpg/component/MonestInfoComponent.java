package com.wegame.mmorpg.component;

import lombok.Data;

@Data
public class MonestInfoComponent {
    /**
     * 怪物类型
     */
    private int type;
    /**
     * 怪物的Hp
     */
    private int hp;
    /**
     * 怪物最大血量
     */
    private int maxHp;
    /**
     * 怪物的攻击力
     */
    private int attack;
    /**
     * 怪物的防御力
     */
    private int def;
    /**
     * 掉经验值
     */
    private int exp;
    /**
     * 移动速度
     */
    private float speed;

}
