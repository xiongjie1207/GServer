package com.wegame.framework.plugin;

public interface IPlugin {
    String getName();

    boolean start();

    boolean stop();
}
