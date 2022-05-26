package org.hegemol.config.common.model;

import java.util.Map;

/**
 * 客户端本地缓存数据
 *
 * @author KevinClair
 **/
public class LocalCacheClientData {

    /**
     * 模拟本地缓存, key为group, value为对应group的配置数据
     */
    private Map<String, String> config;

    private LocalCacheClientData() {

    }

    /**
     * 获取实例
     *
     * @return 单例对象
     */
    public static LocalCacheClientData getInstance() {
        return LocalCacheDataSingleton.INSTANCE;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(final Map<String, String> config) {
        this.config = config;
    }

    private static class LocalCacheDataSingleton {
        private static final LocalCacheClientData INSTANCE = new LocalCacheClientData();
    }
}
