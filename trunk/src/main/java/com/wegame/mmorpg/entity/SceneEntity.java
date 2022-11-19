package com.wegame.mmorpg.entity;

import com.wegame.framework.core.IUpdater;
import com.wegame.mmorpg.component.TransformComponent;
import com.wegame.mmorpg.logic.AISystem;
import com.wegame.mmorpg.logic.AOISystem;
import com.wegame.mmorpg.logic.AStar;
import com.wegame.mmorpg.logic.NavSystem;
import com.wegame.mmorpg.logic.AttackSystem;
import com.wegame.mmorpg.logic.SkillBuffSystem;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author xiongjie
 * @Date 2022/11/18 11:38
 **/
public abstract class SceneEntity implements IUpdater {
    private List<PlayerEntity> playerEntityList;
    private List<MonestEntity> monestEntityList;
    @Getter
    @Setter
    private int mapWidth;
    @Getter
    @Setter
    private int mapHeight;
    @Getter
    @Setter
    private int blockSize;
    @Getter
    @Setter
    private String name;
    private AOISystem aoiSystem;
    @Getter
    private NavSystem navSystem;
    private AttackSystem attackSystem;
    private SkillBuffSystem skillBuffSystem;
    private AISystem aiSystem;
    public SceneEntity(String name,int mapWidth,int mapHeight,int blockSize){
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.blockSize = blockSize;
        this.name=name;
        this.playerEntityList = new ArrayList<>();
        this.monestEntityList = new ArrayList<>();
        this.aoiSystem = new AOISystem();
        this.aoiSystem.init(this.mapWidth,this.blockSize);
        AStar aStar = new AStar();
        aStar.initWithMapData(loadMapData(),this.mapWidth,this.mapHeight,this.blockSize);
        this.navSystem = new NavSystem(aStar);
        this.attackSystem = new AttackSystem();
        this.skillBuffSystem = new SkillBuffSystem();
        this.aiSystem = new AISystem(this);
    }
    public abstract boolean[][] loadMapData();
    public void update(){
        for (PlayerEntity entity:this.playerEntityList){
            this.aoiSystem.update(entity);
            this.navSystem.update(entity);
            this.attackSystem.update(entity);
            this.skillBuffSystem.update(entity);
        }
        for (MonestEntity entity:this.monestEntityList){
            this.navSystem.update(entity);
            this.aiSystem.update(entity);
        }
    }
    public PlayerEntity GetAOIPlayerEntityInRadius(TransformComponent transformComponent, float r) {
        return null;
    }
}
