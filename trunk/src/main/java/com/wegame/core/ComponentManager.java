package com.wegame.core;

import com.wegame.components.IComponent;

import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
    private static ComponentManager instance;
    private List<IComponent> components;
    private ComponentManager(){
        components = new ArrayList<>();
    }
    public static  ComponentManager getInstance(){
        if(instance ==null){
            instance = new ComponentManager();
        }
        return instance;
    }
    public List<IComponent> getComponents(){
        return components;
    }
}
