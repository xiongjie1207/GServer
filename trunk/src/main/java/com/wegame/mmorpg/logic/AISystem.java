package com.wegame.mmorpg.logic;

import com.wegame.mmorpg.component.AIComponent;
import com.wegame.mmorpg.constants.AIType;
import com.wegame.mmorpg.constants.RoleState;
import com.wegame.mmorpg.entity.MonestEntity;
import com.wegame.mmorpg.entity.PlayerEntity;
import com.wegame.mmorpg.entity.SceneEntity;
import com.wegame.mmorpg.model.Vector3;
import com.wegame.util.DeltaTime;

public class AISystem {
    private SceneEntity sceneEntity;
    public AISystem(SceneEntity sceneEntity){
        this.sceneEntity = sceneEntity;
    }
    public void initMonesteAIComponent(MonestEntity entity) {
        if (entity.getAiComponent() == null) {
            entity.setAiComponent(new AIComponent());
        }

        entity.getAiComponent().setAIResult(AIType.Invalid);
        entity.getAiComponent().setNextAITime(0.3f);
        entity.getAiComponent().setPassedTime(0);

        // 可以从Entity里面的怪物信息里面获取;
        entity.getAiComponent().setAttackR(2.0f);
        entity.getAiComponent().setPursueR(8.0f);
        entity.getAiComponent().setBulletR(8.0f);
    }

    public int getAIResult(MonestEntity entity) {
        // 从场景里面寻找距离圈内的PlayerEntity;
        PlayerEntity
            target = this.sceneEntity.GetAOIPlayerEntityInRadius(entity.getTransformInfo(),
            entity.getAiComponent().getPursueR());
        if (target == null) {
            return AIType.Idle;
        }

        entity.getAiComponent().setTarget(target);
        entity.getAiComponent().setNearLen(entity.getTransformInfo().getPosition()
            .distance(target.getTransformInfo().getPosition()));

        if (entity.getAiComponent().getNearLen() <=
            entity.getAiComponent().getAttackR()) { // 后续加攻击;
            return AIType.Idle;
        } else if (entity.getAiComponent().getNearLen() <= entity.getAiComponent().getPursueR()) {
            return AIType.Pursue;
        }

        return AIType.Idle;

    }

    public Vector3 findAStarDstPoint(MonestEntity entity) {
        float len = entity.getShapeInfo().getR() +
            entity.getAiComponent().getTarget().getShapeInfo().getR();
        Vector3 dst = entity.getAiComponent().getTarget().getTransformInfo().getPosition().clone();
        Vector3 src = entity.getTransformInfo().getPosition();
        Vector3 dir = src.sub(dst);
        float dirLen = dir.magnitude();
        dst.setX(dst.getX() + len * (dir.getX() / dirLen));
        dst.setZ(dir.getZ() + len * (dir.getZ() / dirLen));
        return dst;
    }

    public void doActionWithAIResult(MonestEntity entity) {

        switch (entity.getAiComponent().getAIResult()) {
            case AIType.Attack: // 暂时不做攻击;
                entity.setState(RoleState.Attack1);
                break;
            case AIType.Idle:
                entity.setState(RoleState.Idle);
                break;
            case AIType.Pursue:
                entity.setState(RoleState.Run);
                Vector3 dst = findAStarDstPoint(entity);
                sceneEntity.getNavSystem().initAStarComponent(entity,
                    dst);
                break;
        }
    }

    public void update(MonestEntity entity) {
        AIComponent aiComponent = entity.getAiComponent();
        aiComponent.setPassedTime(
            aiComponent.getPassedTime() + DeltaTime.getDeltaTimeSec());
        if (aiComponent.getPassedTime() >= aiComponent.getNextAITime()) {
            aiComponent.setAIResult(getAIResult(entity));
            doActionWithAIResult(entity);
            aiComponent.setPassedTime(0);
        }
    }
}
