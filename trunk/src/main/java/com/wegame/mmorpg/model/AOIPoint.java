package com.wegame.mmorpg.model;

import lombok.Data;

/**
 * @Author xiongjie
 * @Date 2022/11/18 13:22
 **/
@Data
public class AOIPoint {
    private int blockX;
    private int blockY;

    public AOIPoint(int blockX, int blockY) {
        this.blockX = blockX;
        this.blockY = blockY;
    }

}
