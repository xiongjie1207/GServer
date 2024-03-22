package com.wegame.mmorpg.model;

import lombok.Data;

/**
 * @Author xiongjie
 * @Date 2022/11/17 23:56
 **/
@Data
public class Vector2 {
    protected float x;

    protected float z;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(float x, float z) {
        this.x = x;
        this.z = z;
    }
    @Override
    public Vector2 clone() {
        return new Vector2(this.x, this.z);
    }
    public Vector2 sub(Vector2 target) {
        Vector2 dir = new Vector2(0, 0);
        dir.x = this.x - target.x;
        dir.z = this.z - target.z;
        return dir;
    }

    public float distance(Vector2 target) {
        float dx = this.x - target.x;
        float dz = this.z - target.z;
        return (float) Math.sqrt(dx * dx + dz * dz);
    }

    public float degree(Vector2 target) {
        Vector2 dir = this.sub(target);
        float degree = (float) (Math.atan2(dir.z, dir.x) * 180 / Math.PI);
        degree = 360 - degree;
        return degree;
    }

    public float eulerAngleY(Vector2 target) {
        float degree = degree(target);
        return degree + 90;
    }
}
