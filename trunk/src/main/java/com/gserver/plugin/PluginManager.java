package com.gserver.plugin;

import com.gserver.utils.Loggers;
import org.apache.commons.io.FilenameUtils;

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
    private static PluginManager instance;
    private Map<String, Long> jarLastModified;
    private Map<String, IPlugin> pluginMap;

    private PluginManager() {
        jarLastModified = new HashMap<>();
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
        pluginMap.put(String.valueOf(plugin.getName().hashCode()), plugin);
        return true;
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
            Loggers.ErrorLogger.error(e.getMessage(),e);
        }
        return plugins;
    }

    private List<Class<IPlugin>> initJar(File[] jars) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<Class<IPlugin>> plugins = new ArrayList<>();
        for (File jar : jars) {
            Long lastModified = jarLastModified.get(jar.getName());
            long jarDate = jar.lastModified();
            if (lastModified != null) {
                if (jarDate > lastModified.longValue()) {
                    jarLastModified.put(jar.getName(), jarDate);
                } else {
                    break;
                }
            } else {
                jarLastModified.put(jar.getName(), jarDate);

            }
            Loggers.ServerLogger.debug("load jar:" + jar.getPath());
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
