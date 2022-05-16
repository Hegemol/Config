package org.hegemol.config.client.cache;

/**
 * 模拟一下本地缓存数据
 *
 * @author KevinClair
 **/
public class LocalCacheData {

    /**
     * 模拟本地缓存
     */
    private String config;

    private LocalCacheData() {

    }

    /**
     * 获取实例
     *
     * @return 单例对象
     */
    public static LocalCacheData getInstance() {
        return LocalCacheDataSingleton.INSTANCE;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    private static class LocalCacheDataSingleton {
        private static final LocalCacheData INSTANCE = new LocalCacheData();
    }
}
