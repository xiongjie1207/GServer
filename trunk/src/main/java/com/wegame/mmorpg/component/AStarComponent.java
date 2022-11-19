package com.wegame.mmorpg.component;

import com.wegame.mmorpg.model.Vector3;
import lombok.Data;

@Data
public class AStarComponent {
    private Vector3[] roadPoints = null;
    private int nextStep = 0;
    private float vx, vy, vz;
    private float totalTime = 0;
    private float nowTime = 0;
}
