package org.hegemol.config.server.handler;

import org.hegemol.config.common.model.ConfigDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 配置数据变更事件
 *
 * @author KevinClair
 **/
public class DataChangeEvent extends ApplicationEvent {

    private String app;

    private String group;

    public DataChangeEvent(final ConfigDTO source, final String app, final String group) {
        super(source);
        this.app = app;
        this.group = group;
    }

    @Override
    public ConfigDTO getSource() {
        return (ConfigDTO) super.getSource();
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
}
