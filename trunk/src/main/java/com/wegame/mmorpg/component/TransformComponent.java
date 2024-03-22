package com.wegame.mmorpg.component;

import com.wegame.mmorpg.model.Vector3;
import lombok.Data;

@Data
public class TransformComponent {
    private Vector3 position;

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
