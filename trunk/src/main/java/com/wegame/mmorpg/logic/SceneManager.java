package com.wegame.mmorpg.logic;

import com.wegame.framework.core.GameEventLoop;
import com.wegame.framework.core.IUpdater;
import com.wegame.mmorpg.entity.SceneEntity;
import java.util.HashMap;
import java.util.Map;

public class SceneManager implements IUpdater {
    private Map<String, SceneEntity> sceneEntityMap;

    public void addScene(SceneEntity entity){
        this.sceneEntityMap.put(entity.getName(),entity);
    }
    public void removeScene(SceneEntity entity){
        this.sceneEntityMap.remove(entity.getName());
    }
    public SceneEntity findScene(String name){
        return this.sceneEntityMap.get(name);
    }
    private SceneManager() {
        this.sceneEntityMap = new HashMap<>();
        GameEventLoop.getInstance().addUpdater(this);
    }

    @Override
    public void update() {
        for (IUpdater updater:this.sceneEntityMap.values()){
            updater.update();
        }
    }
}
