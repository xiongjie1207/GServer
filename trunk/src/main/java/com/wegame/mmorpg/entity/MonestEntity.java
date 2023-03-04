package com.wegame.mmorpg.entity;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.wegame.mmorpg.component.AIComponent;
import com.wegame.mmorpg.component.AStarComponent;
import com.wegame.mmorpg.component.GhostInfoComponent;
import com.wegame.mmorpg.component.MonestInfoComponent;
import com.wegame.mmorpg.component.ShapeComponent;
import com.wegame.mmorpg.component.TransformComponent;
import com.wegame.mmorpg.constants.RoleState;
import lombok.Data;
import lombok.Getter;

@Data
public class MonestEntity implements CharacterEntity {
    @Protobuf(order = 1, required = true)
    private int id;
    @Protobuf(order = 2, required = true)
    @Getter
    private int state;
    @Protobuf(order = 3, required = true)
    private MonestInfoComponent monestInfo;

    @Protobuf(order = 4, required = true)
    private TransformComponent transformInfo;

    @Protobuf(order = 5, required = true)
    private ShapeComponent shapeInfo;

    private AStarComponent aStarComponent;

    private AIComponent aiComponent;

    private GhostInfoComponent ghostInfoComponent;
    @Override
    public void setState(int state) {
        if (state != RoleState.Run) {
            this.setAStarComponent(null);
        }

        if (this.getState() == RoleState.Idle && state == RoleState.Idle) {
            return;
        }
        this.state = state;
    }

    public GhostInfoComponent getGhostInfo() {
        return this.ghostInfoComponent;
    }

    public void setGhostInfo(GhostInfoComponent ghostInfo) {
        this.ghostInfoComponent = ghostInfo;
    }
}
