package com.wegame.framework.core;

import com.wegame.framework.plugin.IComponent;

import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private static PluginManager instance;
    private final List<IComponent> plugins;

    private PluginManager() {
        plugins = new ArrayList<>();
    }

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public List<IComponent> getPlugins() {
        return plugins;
    }
}
