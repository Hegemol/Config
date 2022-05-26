package org.hegemol.config.common.model;

/**
 * 本地缓存配置
 *
 * @author KevinClair
 **/
public class ConfigLocalCache {

    /**
     * 配置组
     */
    private String group;

    /**
     * 对应配置组的配置信息
     */
    private String config;

    public ConfigLocalCache() {
    }

    public ConfigLocalCache(final String group, final String config) {
        this.group = group;
        this.config = config;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }
}
