package org.hegemol.config.server.model;

import java.util.Date;

/**
 * TODO
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
     * 配置内容
     */
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
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
}
