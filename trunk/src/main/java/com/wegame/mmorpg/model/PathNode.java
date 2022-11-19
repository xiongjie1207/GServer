package com.wegame.mmorpg.model;

import lombok.Data;

@Data
public class PathNode {
    /**
     * 从起点走到目前点的消耗
     */
    private float gCost;
    /**
     * 预估的从目前点到终点的消耗
     */
    private float hCost;
    /**
     * 总计消耗
     */
    private float totalCost;
    /**
     * 是否可通过
     */
    private boolean traversable;
    /**
     * 上一个节点，父节点
     */
    private PathNode lastStepGrid;
    private int indexI;
    private int indexJ;
}
