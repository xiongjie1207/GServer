package com.gserver.plugin;

public interface IPlugin {
    String getName();

    boolean start();

    boolean stop();
}
