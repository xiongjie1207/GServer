package com.wegame.framework.plugin;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author xiongjie
 * @Date 2023/07/04 11:44
 **/
@Slf4j
class PluginClassLoader extends URLClassLoader {
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    private final List<IPlugin> plugins = new ArrayList<>();
    public Map<String, Class<?>> getLoadedClasses() {
        return loadedClasses;
    }
    void addPlugin(IPlugin plugin){
        this.plugins.add(plugin);
    }
    public PluginClassLoader(URL[] urls) {
        super(urls, PluginClassLoader.class.getClassLoader());
    }
    @Override
    protected Class<?> findClass(String name) {
        try {
            // 从已加载的类集合中获取指定名称的类
            Class<?> clazz = loadedClasses.get(name);
            if (clazz != null) {
                return clazz;
            }
            // 调用父类的findClass方法加载指定名称的类
            clazz = super.findClass(name);
            // 将加载的类添加到已加载的类集合中
            loadedClasses.put(name, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    public void unload() {
        try {
            plugins.forEach(IPlugin::uninstall);
            plugins.clear();
            // 从其父类加载器的加载器层次结构中移除该类加载器
            close();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }


}

