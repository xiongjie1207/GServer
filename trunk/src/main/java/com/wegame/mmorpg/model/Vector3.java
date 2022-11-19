package com.wegame.mmorpg.model;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import lombok.Data;

@Data
public class Vector3 extends Vector2{
    @Protobuf(order = 2, required = true)
    private float y;


    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(float x, float y, float z) {
        super(x,z);
        this.y = y;
    }
    @Override
    public Vector3 clone() {
        return new Vector3(this.x, this.y, this.z);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 sub(Vector3 target) {
        Vector3 dir = new Vector3(0, 0, 0);
        dir.x = this.x - target.x;
        dir.y = this.y - target.y;
        dir.z = this.z - target.z;
        return dir;
    }

    public float distance(Vector3 target) {
        float dx = this.x - target.x;
        float dy = this.y - target.y;
        float dz = this.z - target.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
