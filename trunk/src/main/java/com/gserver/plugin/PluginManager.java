package com.gserver.plugin;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {
    Logger logger = Logger.getLogger(PluginManager.class);
    private static PluginManager instance;
    private Map<String, File> jarCache;
    private Map<String, IPlugin> pluginMap;

    private PluginManager() {
        jarCache = new HashMap<>();
        pluginMap = new HashMap<>();
    }


    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    public Collection<IPlugin> listPlugin() {
        return this.pluginMap.values();
    }

    public boolean addPlugin(IPlugin plugin) {
        if (pluginMap.containsKey(String.valueOf(plugin.getName().hashCode()))) {
            return false;
        }
        boolean ret = plugin.start();
        if (ret) {
            pluginMap.put(String.valueOf(plugin.getName().hashCode()), plugin);
        }
        return ret;
    }


    public void removePlugin(IPlugin plugin) {
        pluginMap.remove(String.valueOf(plugin.getName().hashCode()));
    }

    public List<Class<IPlugin>> loadPluginFromJar(String pluginsDir) {
        List<Class<IPlugin>> plugins = null;
        File dir = new File(pluginsDir);
        File[] jars = dir.listFiles(pathname -> {
            String basename = FilenameUtils.getExtension(pathname.getName());
            if (basename.equalsIgnoreCase("jar")) {
                return true;
            } else {
                return false;
            }
        });
        try {
            if (jars != null) {
                plugins = initJar(jars);
            }
            if (plugins == null) {
                plugins = new ArrayList<>();
            }
        } catch (Exception e) {
            logger.error(e.getCause(), e);
        }
        return plugins;
    }

    private List<Class<IPlugin>> initJar(File[] jars) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<Class<IPlugin>> plugins = new ArrayList<>();
        for (File jar : jars) {
            File cacheJar = jarCache.get(jar.getName());
            if (cacheJar != null) {
                if (jar.lastModified() > cacheJar.lastModified()) {
                    jarCache.put(jar.getName(), jar);
                } else {
                    break;
                }
            } else {
                jarCache.put(jar.getName(), jar);

            }
            logger.debug("load jar:" + jar.getPath());
            URL url = new URL("file:" + jar.getPath());
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread()
                    .getContextClassLoader());
            String pluginPath = "Plugin";
            Class<IPlugin> clazz = (Class<IPlugin>) classLoader.loadClass(pluginPath);
            plugins.add(clazz);
        }
        return plugins;
    }

}
