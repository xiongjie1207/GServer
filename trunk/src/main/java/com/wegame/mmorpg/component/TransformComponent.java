package com.wegame.mmorpg.component;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.wegame.mmorpg.model.Vector3;
import lombok.Data;

@Data
public class TransformComponent {
    @Protobuf(order = 1, required = true)
    private Vector3 position;

    @Protobuf(order = 2, required = true)
    private float eulerAngleY = 0;

    @Override
    public TransformComponent clone() {
        TransformComponent t = new TransformComponent();
        t.position = this.position.clone();
        t.eulerAngleY = this.eulerAngleY;
        return t;
    }

    public void LookAt(Vector3 point) {
        this.eulerAngleY = this.position.eulerAngleY(point);
    }
}
