package com.wegame.mmorpg.entity;

import com.wegame.framework.packet.IPacket;
import com.wegame.mmorpg.component.*;
import com.wegame.mmorpg.constants.RoleState;
import lombok.Data;

@Data
public class PlayerEntity implements CharacterEntity {
    private int id;
    private int state;
    // 玩家私密的数据是不必要的;
    private PlayerInfoComponent playerInfo;
    private EquipmentComponent equipmentInfo;

    private TransformComponent transformInfo;



    private AOIComponent aoiComponent;
    private SkillBuffComponent skillBuffComponent;

    private AttackComponent attackComponent;
    private ShapeComponent shapeInfo;
    private AStarComponent aStarComponent;
    private JoystickComponent joystickComponent;

    public PlayerEntity() {
        this.playerInfo = null;
        this.equipmentInfo = null;
        this.shapeInfo = null;
        this.transformInfo = null;
        this.aStarComponent = null;
        this.joystickComponent = null;
        this.attackComponent = null;
        this.skillBuffComponent = null;
    }


    public static PlayerEntity create() {
        PlayerEntity entity = new PlayerEntity();
        entity.playerInfo = new PlayerInfoComponent();
        entity.equipmentInfo = new EquipmentComponent();
        entity.shapeInfo = new ShapeComponent();
        entity.transformInfo = new TransformComponent();
        entity.aoiComponent = new AOIComponent();

        entity.aStarComponent = null;
        entity.joystickComponent = null;
        entity.attackComponent = null;

        entity.shapeInfo.setHeight(2.0f);
        entity.shapeInfo.setR(0.5f);


        return entity;
    }

    public void sendMessage(IPacket msg) {

    }

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
}




