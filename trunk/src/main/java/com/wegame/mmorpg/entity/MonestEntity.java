package com.wegame.mmorpg.entity;

import com.wegame.mmorpg.component.*;
import com.wegame.mmorpg.constants.RoleState;
import lombok.Data;

@Data
public class MonestEntity implements CharacterEntity {
    private int id;
    private int state;
    private MonestInfoComponent monestInfo;

    private TransformComponent transformInfo;

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
