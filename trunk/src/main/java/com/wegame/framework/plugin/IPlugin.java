package com.wegame.framework.plugin;

/**
 * @Author xiongjie
 * @Date 2023/07/04 11:56
 **/
public interface IPlugin {
    void install();
    void uninstall();

    int order();
}
