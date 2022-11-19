package com.wegame.mmorpg.component;


import com.wegame.mmorpg.entity.PlayerEntity;
import lombok.Data;

@Data
public class AIComponent {

    private float passedTime; // 距离一次做决策过去的时间;
    private float nextAITime; // 下一次做决策的到达时间;

    private int AIResult; // 上一次做的决策, 决策类型的枚举;

    private float pursueR; // 追击半径
    private float attackR; // 攻击半径
    private float bulletR; // 子弹模式攻击半径;

    // 临时数据
    private float nearLen; // 找出来的最小的距离就放这个变量里面，不用重复计算;
    private PlayerEntity target; // 距离最近的一个怪物;
}
