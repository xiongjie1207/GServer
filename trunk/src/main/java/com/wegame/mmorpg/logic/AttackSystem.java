package com.wegame.mmorpg.logic;

import com.wegame.mmorpg.component.AttackComponent;
import com.wegame.mmorpg.entity.PlayerEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttackSystem {
    public void initAttackComponent(PlayerEntity entity, float duration,
                                           float computeHurtTime,
                                           int attackType, boolean isLoop) {
        entity.setAttackComponent(new AttackComponent());
        AttackComponent attackComponent = entity.getAttackComponent();
        attackComponent.setDuration(duration);
        attackComponent.setComputeHurtTime(computeHurtTime);
        attackComponent.setAttackType(attackType);
        attackComponent.setNowTime(0);
        attackComponent.setLoop(isLoop);
        attackComponent.setComputed(false);
        if (attackComponent.getTarget() != null) {
            entity.getTransformInfo()
                .LookAt(attackComponent.getTarget().getTransformInfo().getPosition());
        }
    }

    public void doAttackHurtCompute(PlayerEntity entity) {
        // 从配置里面读取招数的杀伤半径
        float R = 2.5f;

    }

    public void doEndAttack(PlayerEntity entity) {

    }

    public void update(PlayerEntity entity) {

    }
}
