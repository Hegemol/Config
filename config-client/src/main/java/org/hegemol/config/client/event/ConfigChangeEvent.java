package org.hegemol.config.client.event;

import org.springframework.context.ApplicationEvent;

/**
 * 配置变更事件
 *
 * @author KevinClair
 **/
public class ConfigChangeEvent extends ApplicationEvent {

    /**
     * 配置组
     */
    private String group;

    /**
     * 配置数据
     */
    private String config;

    public ConfigChangeEvent(final Object source, final String group, final String config) {
        super(source);
        this.group = group;
        this.config = config;
    }

    /**
     * Gets the value of group.
     *
     * @return the value of group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group group
     */
    public void setGroup(final String group) {
        this.group = group;
    }

    /**
     * Gets the value of config.
     *
     * @return the value of config
     */
    public String getConfig() {
        return config;
    }

    /**
     * Sets the config.
     *
     * @param config config
     */
    public void setConfig(final String config) {
        this.config = config;
    }
}
