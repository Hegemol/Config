package org.hegemol.config.common.model;

/**
 * 配置数据的md5值
 *
 * @author KevinClair
 **/
public class Md5Config {

    /**
     * 配置组
     */
    private String group;

    /**
     * 当前配置组的配置值md5之后的数据
     */
    private String md5Config;

    public Md5Config(final String group, final String md5Config) {
        this.group = group;
        this.md5Config = md5Config;
    }

    public Md5Config() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public String getMd5Config() {
        return md5Config;
    }

    public void setMd5Config(final String md5Config) {
        this.md5Config = md5Config;
    }
}
