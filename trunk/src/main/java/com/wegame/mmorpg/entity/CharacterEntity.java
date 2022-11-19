package com.wegame.mmorpg.entity;

import com.wegame.mmorpg.component.AStarComponent;
import com.wegame.mmorpg.component.ShapeComponent;
import com.wegame.mmorpg.component.TransformComponent;

/**
 * @Author xiongjie
 * @Date 2022/11/16 12:00
 **/
public interface CharacterEntity {
    int getState();

    void setState(int state);

    int getId();

    void setId(int id);

    AStarComponent getAStarComponent();

    void setAStarComponent(AStarComponent astarComponent);

    TransformComponent getTransformInfo();

    void setTransformInfo(TransformComponent transformInfo);

    ShapeComponent getShapeInfo();

    void setShapeInfo(ShapeComponent shapeInfo);

}
