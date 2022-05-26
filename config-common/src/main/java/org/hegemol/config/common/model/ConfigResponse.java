package org.hegemol.config.common.model;

/**
 * 配置传输对象
 *
 * @author KevinClair
 **/
public class ConfigResponse {

    private String app;

    private String group;

    private String config;

    public ConfigResponse(final String app, final String config, final String group) {
        this.app = app;
        this.config = config;
        this.group = group;
    }

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
