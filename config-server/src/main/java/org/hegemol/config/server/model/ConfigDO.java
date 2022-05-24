package org.hegemol.config.server.model;

import java.util.Date;

/**
 * config数据库映射对象
 *
 * @author KevinClair
 **/
public class ConfigDO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 应用名
     */
    private String app;

    /**
     * 组
     */
    private String group;

    /**
     * 配置内容
     */
    private String config;

    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
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

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(final Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }
}
