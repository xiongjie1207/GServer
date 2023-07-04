package com.wegame.framework.core;

import com.wegame.framework.component.IComponent;

import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
    private static ComponentManager instance;
    private final List<IComponent> components;

    private ComponentManager() {
        components = new ArrayList<>();
    }

    public static ComponentManager getInstance() {
        if (instance == null) {
            instance = new ComponentManager();
        }
        return instance;
    }

    public List<IComponent> getComponents() {
        return components;
    }
}
