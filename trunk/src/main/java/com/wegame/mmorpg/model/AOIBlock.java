package com.wegame.mmorpg.model;

import com.wegame.mmorpg.entity.PlayerEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiongjie
 * @Date 2022/11/18 13:22
 **/
public class AOIBlock {
    @Getter
    private final List<PlayerEntity> entities; // 当前块有哪些玩家对象;

    public AOIBlock() {
        this.entities = new ArrayList<>();
    }
}
