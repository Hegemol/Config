package org.hegemol.config.common.model;

import java.util.Map;

/**
 * 本地缓存服务端
 *
 * @author KevinClair
 **/
public class LocalCacheServerData {

    /**
     * 第一层map，key为应用名
     * 第二层map，key为group组，value为对应group组的value值
     */
    private Map<String, Map<String, String>> data;

    private LocalCacheServerData() {

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static LocalCacheServerData getInstance() {
        return LocalCacheDataSingleton.INSTANCE;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(final Map<String, Map<String, String>> data) {
        this.data = data;
    }

    static class LocalCacheDataSingleton {
        // 实例
        private static final LocalCacheServerData INSTANCE = new LocalCacheServerData();
    }
}
