package org.hegemol.config.common.model;

/**
 * 客户端本地缓存数据
 *
 * @author KevinClair
 **/
public class LocalCacheClientData {

    /**
     * 模拟本地缓存
     */
    private String config;

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

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    private static class LocalCacheDataSingleton {
        private static final LocalCacheClientData INSTANCE = new LocalCacheClientData();
    }
}
