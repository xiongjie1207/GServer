package com.wegame.mmorpg.logic;


import com.wegame.mmorpg.component.AStarComponent;
import com.wegame.mmorpg.constants.RoleState;
import com.wegame.mmorpg.entity.CharacterEntity;
import com.wegame.mmorpg.entity.MonestEntity;
import com.wegame.mmorpg.entity.PlayerEntity;
import com.wegame.mmorpg.model.Vector3;
import com.wegame.utils.DeltaTime;

public class NavSystem {
    private AStar aStar;
    public NavSystem(AStar aStar){
        this.aStar = aStar;
    }
    public boolean initAStarComponent(CharacterEntity entity, Vector3 dst) {
        Vector3 src = entity.getTransformInfo().getPosition().clone();
        if (!aStar.astarSearch(src, dst, 0)) {
            return false;
        }

        Vector3[] roads = aStar.getFilterRoadData();
        entity.setAStarComponent(new AStarComponent());
        entity.getAStarComponent().setRoadPoints(roads);
        entity.getAStarComponent().getRoadPoints()[0] = src;
        entity.getAStarComponent().getRoadPoints()[
            entity.getAStarComponent().getRoadPoints().length - 1] = dst;
        entity.getAStarComponent().setNextStep(1);
        walkToNext(entity);

        return true;
    }

    private void walkToNext(CharacterEntity entity) {
        AStarComponent astarComponet = entity.getAStarComponent();
        if (astarComponet.getNextStep() >= astarComponet.getRoadPoints().length) {
            entity.setAStarComponent(null);
            entity.setState(RoleState.Idle);
            return;
        }

        Vector3 src = entity.getTransformInfo().getPosition();
        Vector3 dst = astarComponet.getRoadPoints()[astarComponet.getNextStep()];
        Vector3 dir = dst.sub(src);

        float len = src.distance(dst);
        if (len <= 0) {
            astarComponet.setNextStep(astarComponet.getNextStep() + 1);
            walkToNext(entity);
            return;
        }

        astarComponet.setNowTime(0);
        if(entity instanceof MonestEntity){
            MonestEntity monestEntity = (MonestEntity)entity;
            astarComponet.setTotalTime(len / monestEntity.getGhostInfo().getSpeed());
            astarComponet.setVx(monestEntity.getGhostInfo().getSpeed() * dir.getX() / len);
            astarComponet.setVy(monestEntity.getGhostInfo().getSpeed() * dir.getY() / len);
            astarComponet.setVz(monestEntity.getGhostInfo().getSpeed() * dir.getZ() / len);
        }else{
            PlayerEntity playerEntity = (PlayerEntity)entity;
            astarComponet.setTotalTime(len / playerEntity.getPlayerInfo().getSpeed());
            astarComponet.setVx(playerEntity.getPlayerInfo().getSpeed() * dir.getX() / len);
            astarComponet.setVy(playerEntity.getPlayerInfo().getSpeed() * dir.getY() / len);
            astarComponet.setVz(playerEntity.getPlayerInfo().getSpeed() * dir.getZ() / len);
        }


        float degree = (float) (Math.atan2(dir.getZ(), dir.getX()) * 180 / Math.PI);
        degree = 360 - degree;
        entity.getTransformInfo().setEulerAngleY(degree + 90);
    }

    public void update(CharacterEntity entity) {
        AStarComponent astar = entity.getAStarComponent();
        float dt = DeltaTime.getDeltaTimeSec();

        astar.setNowTime(astar.getNowTime() + dt);
        if (astar.getNowTime() > astar.getTotalTime()) {
            dt -= (astar.getNowTime() - astar.getTotalTime());
        }

        Vector3 pos = entity.getTransformInfo().getPosition();
        pos.setX(pos.getX() + astar.getVx() * dt);
        pos.setY(pos.getY() + astar.getVy() * dt);
        pos.setZ(pos.getZ() + astar.getVz() * dt);

        if (astar.getNowTime() >= astar.getTotalTime()) {
            astar.setNextStep(astar.getNextStep() + 1);
            walkToNext(entity);
        }

    }
}
