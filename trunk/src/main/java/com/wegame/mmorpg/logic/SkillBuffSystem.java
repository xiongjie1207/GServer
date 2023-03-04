package com.wegame.mmorpg.logic;

import com.wegame.mmorpg.component.SkillBuffComponent;
import com.wegame.mmorpg.entity.PlayerEntity;
import com.wegame.utils.DeltaTime;
import java.util.HashMap;
import java.util.Map.Entry;

public class SkillBuffSystem {
    // skillId --->time;
    public HashMap<Integer, Float> totalTimeMap;
    public SkillBuffSystem(){
        totalTimeMap = new HashMap<>();
    }
    public void resetSkillBufTime(PlayerEntity entity, int skillId) {
        SkillBuffComponent skillBuffComponent = entity.getSkillBuffComponent();
        if (!skillBuffComponent.buffTimeMap.containsKey(skillId)) {
            return;
        }

        skillBuffComponent.buffTimeMap.put(skillId, 0.0f);
    }

    public boolean isPlayerEntityCanAttack(PlayerEntity entity, int skillId) {
        SkillBuffComponent skillBuffComponent = entity.getSkillBuffComponent();
        if (!skillBuffComponent.buffTimeMap.containsKey(skillId)) {
            return true;
        }

        float buffTime = skillBuffComponent.buffTimeMap.get(skillId);
        float total = totalTimeMap.get(skillId);
        return buffTime >= total;
    }

    public void initSkilBuffComponent(PlayerEntity entity) {
    }

    public void update(PlayerEntity entity) {
        SkillBuffComponent skillBuffComponent = entity.getSkillBuffComponent();
        for (Entry<Integer, Float> entry : skillBuffComponent.buffTimeMap.entrySet()) {
            Integer id = entry.getKey();
            float total = totalTimeMap.get(id);
            float buffTime = entry.getValue();

            if (entity.getAttackComponent() != null &&
                entity.getAttackComponent().getAttackType() == id) {
                continue;
            }

            if (buffTime >= total) {
                continue;
            }
            buffTime += DeltaTime.getDeltaTimeSec();
            skillBuffComponent.buffTimeMap.put(id, buffTime);
            if (buffTime >= total) {
            }

        }
    }
}
