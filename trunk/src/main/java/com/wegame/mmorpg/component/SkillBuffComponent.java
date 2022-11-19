package com.wegame.mmorpg.component;

import java.util.HashMap;

public class SkillBuffComponent {
    public HashMap<Integer, Float> buffTimeMap;

    public SkillBuffComponent() {
        this.buffTimeMap = new HashMap<>();
    }
}
