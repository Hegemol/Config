package org.hegemol.config.client.event;

import org.hegemol.config.common.model.ConfigChangeFieldModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置变更管理器，负责管理分组下的需要动态刷新的配置key
 *
 * @author KevinClair
 **/
public class ConfigChangeManager {

    /**
     * 全局配置变更管理map，key为group，value为每个group对应的{@link ConfigChangeManager}
     */
    private static final Map<String, ConfigChangeManager> CHANGE_MANAGER_MAP = new ConcurrentHashMap<>();

    /**
     * 当前group的变更管理map，key为需要动态刷新的配置key，value为对应key的类对象以及属性名{@link ConfigChangeFieldModel}
     */
    private final Map<String, ConfigChangeFieldModel> PROPERTY_CHANGE_MANAGER_MAP = new ConcurrentHashMap<>();

    /**
     * 添加配置变更
     *
     * @param group                  配置分组
     * @param configKey              配置key
     * @param configChangeFieldModel 配置key对应的类对象以及属性名{@link ConfigChangeFieldModel}
     */
    public static void addChangeField(String group, String configKey, ConfigChangeFieldModel configChangeFieldModel) {
        ConfigChangeManager configChangeManager = CHANGE_MANAGER_MAP.getOrDefault(group, new ConfigChangeManager());
        configChangeManager.PROPERTY_CHANGE_MANAGER_MAP.put(configKey, configChangeFieldModel);
        CHANGE_MANAGER_MAP.put(group, configChangeManager);
    }

    /**
     * 获取对应group的配置变更管理器
     *
     * @param group 分组名
     * @return 配置变更管理器 {@link ConfigChangeManager}
     */
    public static ConfigChangeManager getListenerManager(String group) {
        return CHANGE_MANAGER_MAP.get(group);
    }

    /**
     * 获取当前group的配置变更管理器的配置变更Map
     *
     * @return PROPERTY_CHANGE_MANAGER_MAP
     */
    public Map<String, ConfigChangeFieldModel> getPropertyChangeManagerMap() {
        return PROPERTY_CHANGE_MANAGER_MAP;
    }
}
