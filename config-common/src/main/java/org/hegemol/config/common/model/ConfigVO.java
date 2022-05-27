package org.hegemol.config.common.model;

/**
 * 配置传输对象
 *
 * @author KevinClair
 **/
public class ConfigVO {

    private String app;

    private String config;

    private String group;

    public String getApp() {
        return app;
    }

    public void setApp(final String app) {
        this.app = app;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }
}
