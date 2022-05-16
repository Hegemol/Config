package org.hegemol.config.client;

import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 长轮询请求服务
 *
 * @author KevinClair
 **/
public class HttpLongPollingService implements InitializingBean {

    /**
     * 状态标识，为true时代表请求正在运行
     */
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private final RestTemplate restTemplate;

    private final HttpLongPollingConfigurationProperties configurationProperties;

    public HttpLongPollingService(final RestTemplate restTemplate, final HttpLongPollingConfigurationProperties configurationProperties) {
        this.restTemplate = restTemplate;
        this.configurationProperties = configurationProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    class HttpLongPollingTask implements Runnable {

        /**
         * 请求服务
         */
        private String url;

        public HttpLongPollingTask(final String url) {
            this.url = url;
        }

        @Override
        public void run() {
            // 当正在运行时，无限循环
            while (RUNNING.get()) {

            }

        }
    }
}
