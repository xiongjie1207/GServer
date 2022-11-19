package com.wegame.framework.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 所有策划配置的数据池
 *
 * @author kingston
 */
@Slf4j
public class ConfigDatasPool {

    private static final ConfigDatasPool instance = new ConfigDatasPool();
    private final ConcurrentMap<Class<?>, Reloadable> datas = new ConcurrentHashMap<>();

    private ConfigDatasPool() {
    }

    public static ConfigDatasPool getInstance() {
        return instance;
    }

    /**
     * 起服读取所有的配置数据
     */
    public void loadAllConfigs() {

    }

    public <V> V getStorage(Class<?> config) {
        return (V) datas.get(config);
    }

    /**
     * 单表重载
     *
     * @param configTableName 配置表名称
     */
    public boolean reload(String configTableName) {
        for (Map.Entry<Class<?>, Reloadable> entry : datas.entrySet()) {
            Class<?> c = entry.getKey();
            if (c.getSimpleName().toLowerCase().indexOf(configTableName.toLowerCase()) >= 0) {
                try {
                    Reloadable storage = (Reloadable) c.newInstance();
                    storage.reload();
                    datas.put(c, storage);
                    return true;
                } catch (Exception e) {
                    log.error(c.getName() + "配置数据重载异常", e);
                }
                break;
            }
        }
        return false;
    }

}
