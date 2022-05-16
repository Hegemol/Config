package org.hegemol.config.client;

import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
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
    @Bean
    public RestTemplate restTemplate(HttpLongPollingConfigurationProperties configurationProperties) {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setConnectTimeout(configurationProperties.getConnectionTimeout());
        factory.setReadTimeout(configurationProperties.getReadTimeout());
        factory.setWriteTimeout(configurationProperties.getWriteTimeout());
        return new RestTemplate(factory);
    }

    /**
     * 初始化http长轮询请求服务
     *
     * @param restTemplate            rest模板
     * @param configurationProperties 配置文件
     * @param environment             环境变量
     * @return Http长轮询服务 {@link HttpLongPollingService}
     */
    @Bean
    public HttpLongPollingService httpLongPollingService(RestTemplate restTemplate, HttpLongPollingConfigurationProperties configurationProperties, ConfigurableEnvironment environment) {
        return new HttpLongPollingService(restTemplate, configurationProperties, environment.getProperty("spring.application.name"));
    }
}

