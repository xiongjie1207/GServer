package com.wegame.mmorpg.component;

import com.wegame.mmorpg.entity.CharacterEntity;
import lombok.Data;

@Data
public class AttackComponent {
    /**
     * 攻击的动画总时间
     */
    private float duration;
    /**
     * 计算伤害的时间点
     */
    private float computeHurtTime;
    /**
     * 攻击类型
     */
    private int attackType;
    /**
     * 累计过去的时间
     */
    private float nowTime;
    /**
     * 是否已经计算过伤害
     */
    private boolean isComputed;
    /**
     * 是否为连环攻击模式
     */
    private boolean isLoop;
    /**
     * 攻击的目标
     */
    private CharacterEntity target;
}
