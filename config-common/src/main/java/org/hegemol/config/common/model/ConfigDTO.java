package org.hegemol.config.common.model;

/**
 * 配置传输对象
 *
 * @author KevinClair
 **/
public class ConfigDTO {

    private String app;

    private String group;

    private String config;

    public ConfigDTO() {
    }

    public ConfigDTO(final String app, final String group, final String config) {
        this.app = app;
        this.group = group;
        this.config = config;
    }

    public String getApp() {
        return app;
    }

    public void setApp(final String app) {
        this.app = app;
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
