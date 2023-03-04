package com.wegame.mmorpg.logic;

import com.wegame.mmorpg.component.JoystickComponent;
import com.wegame.mmorpg.constants.RoleState;
import com.wegame.mmorpg.entity.PlayerEntity;
import com.wegame.mmorpg.model.Vector3;
import com.wegame.utils.DeltaTime;

public class JoystickSystem {
    public void initJoystickComponent(PlayerEntity entity, AStar astar,
                                             int xpos, int ypos) {
        if (xpos == 0 && ypos == 0) {
            entity.setJoystickComponent(null);

            entity.setState(RoleState.Idle);
            return;
        }


        if (entity.getJoystickComponent() == null) {
            entity.setJoystickComponent(new JoystickComponent());
        }

        entity.getJoystickComponent().setAStar(astar);
        entity.getJoystickComponent().setXPos(xpos);
        entity.getJoystickComponent().setYPos(ypos);

        // 角色的方向
        float degree = (float) (Math.atan2(ypos, xpos) * 180 / Math.PI);
        degree = 360 - degree;
        entity.getTransformInfo().setEulerAngleY(degree + 90);
        // end
    }

    public void update(PlayerEntity entity) {
        float xpos = ((float) (entity.getJoystickComponent().getXPos())) / (1 << 16);
        float ypos = ((float) (entity.getJoystickComponent().getYPos())) / (1 << 16);
        float speed = entity.getPlayerInfo().getSpeed();
        float vx = speed * xpos; // cos
        float vz = speed * ypos; // sin;
        float dt = DeltaTime.getDeltaTimeSec();
        Vector3 pos = entity.getTransformInfo().getPosition().clone();
        pos.setX(pos.getX() + vx * dt);
        pos.setZ(pos.getZ() + vz * dt);
        if (!entity.getJoystickComponent().getAStar().isWordPosCanGo(pos)) {
            return;
        }
        entity.getTransformInfo().getPosition().setX(pos.getX());
        entity.getTransformInfo().getPosition().setZ(pos.getZ());
    }
}
