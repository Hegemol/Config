package org.hegemol.config.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 长轮询请求配置
 *
 * @author KevinClair
 **/
@ConfigurationProperties(prefix = "http")
public class HttpLongPollingConfigurationProperties {

    /**
     * 请求url
     */
    private String url;

    /**
     * 每次轮询的延迟时间
     */
    private Integer delayTime;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeout = 10;

    /**
     * 写超时时间
     */
    private Integer writeTimeout = 90;

    /**
     * 读超时时间
     */
    private Integer readTimeout = 90;

    public HttpLongPollingConfigurationProperties(final String url, final Integer delayTime, final Integer connectionTimeout, final Integer writeTimeout, final Integer readTimeout) {
        this.url = url;
        this.delayTime = delayTime;
        this.connectionTimeout = connectionTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }

    public HttpLongPollingConfigurationProperties() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(final Integer delayTime) {
        this.delayTime = delayTime;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(final Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(final Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
}
