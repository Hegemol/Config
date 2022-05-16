package org.hegemol.config.client;

import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 长轮询自动装配
 *
 * @author KevinClair
 **/
@Configuration
@ConditionalOnProperty(prefix = "http", name = "url")
@EnableConfigurationProperties(HttpLongPollingConfigurationProperties.class)
public class HttpLongPollingAutoConfiguration {

    /**
     * 初始化RestTemplate，用于发送http请求
     *
     * @param configurationProperties 配置类 {@link HttpLongPollingConfigurationProperties}
     * @return {@link RestTemplate}
     */
    public RestTemplate restTemplate(HttpLongPollingConfigurationProperties configurationProperties) {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setConnectTimeout(configurationProperties.getConnectionTimeout());
        factory.setReadTimeout(configurationProperties.getReadTimeout());
        factory.setWriteTimeout(configurationProperties.getWriteTimeout());
        return new RestTemplate(factory);
    }
}

