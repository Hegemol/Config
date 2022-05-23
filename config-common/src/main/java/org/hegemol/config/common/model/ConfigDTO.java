package org.hegemol.config.common.model;

/**
 * 配置传输对象
 *
 * @author KevinClair
 **/
public class ConfigDTO {

    private String app;

    private String config;

    public ConfigDTO(final String app, final String config) {
        this.app = app;
        this.config = config;
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
}
