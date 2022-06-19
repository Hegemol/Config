package org.hegemol.config.client;

import org.hegemol.config.client.config.ConfigInitManager;
import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.hegemol.config.client.event.ConfigChangeListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
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
    @Bean
    public RestTemplate restTemplate(HttpLongPollingConfigurationProperties configurationProperties) {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setConnectTimeout((int) configurationProperties.getConnectionTimeout());
        factory.setReadTimeout((int) configurationProperties.getReadTimeout());
        factory.setWriteTimeout((int) configurationProperties.getWriteTimeout());
        return new RestTemplate(factory);
    }

    /**
     * 初始化http长轮询请求服务
     *
     * @param restTemplate              rest模板
     * @param configProperties          配置中心的配置文件
     * @param applicationEventPublisher 事件发布
     * @return Http长轮询服务 {@link HttpLongPollingService}
     */
    @Bean
    public HttpLongPollingService httpLongPollingService(RestTemplate restTemplate,
                                                         HttpLongPollingConfigurationProperties configProperties,
                                                         ApplicationEventPublisher applicationEventPublisher) {
        return new HttpLongPollingService(restTemplate, configProperties, applicationEventPublisher);
    }

    /**
     * 配置初始化管理器
     *
     * @param httpLongPollingService http长轮询服务(前置关系)
     * @return {@link ConfigInitManager}
     */
    @Bean
    public ConfigInitManager configInitManager(HttpLongPollingService httpLongPollingService) {
        return new ConfigInitManager();
    }

    /**
     * 配置变更事件监听器
     *
     * @return {@link ConfigChangeListener}
     */
    @Bean
    public ConfigChangeListener configChangeListener() {
        return new ConfigChangeListener();
    }
}

