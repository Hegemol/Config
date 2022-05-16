package org.hegemol.config.client;

import org.hegemol.config.client.cache.LocalCacheData;
import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.hegemol.config.common.constant.Constants;
import org.hegemol.config.common.utils.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 长轮询请求服务
 *
 * @author KevinClair
 **/
public class HttpLongPollingService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpLongPollingService.class);

    /**
     * 状态标识，为true时代表请求正在运行
     */
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private final RestTemplate restTemplate;

    private final HttpLongPollingConfigurationProperties configurationProperties;

    private final String app;

    public HttpLongPollingService(final RestTemplate restTemplate, final HttpLongPollingConfigurationProperties configurationProperties, final String app) {
        this.restTemplate = restTemplate;
        this.configurationProperties = configurationProperties;
        this.app = app;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * 发起长轮询请求
     *
     * @param url 请求的url地址
     */
    private void doLongPolling(final String url) {
        // 获取本地缓存的数据
        String config = LocalCacheData.getInstance().getConfig();
        // 初始化请求参数
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>(8);
        // 当前的请求应用
        requestParam.put("app", Arrays.asList(app));
        // 放入缓存数据
        requestParam.put("config", Arrays.asList(Md5Utils.md5(config)));

        // 拼装请求参数
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParam, headers);
        String listenerUrl = url + Constants.SERVER_LISTENER_URL;

        // 发送请求
        String json = this.restTemplate.postForEntity(listenerUrl, httpEntity, String.class).getBody();
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
                // 初始化重试次数
                int retryTimes = 3;
                for (int i = 0; i < 3; i++) {
                    try {
                        doLongPolling(url);
                    } catch (Exception e) {
                        // 如果当前请求次数小于最大重试次数，则继续重试
                        if (i < retryTimes) {
                            logger.error("Http长轮询请求异常，当前重试次数：{}，异常信息：{}", i, e);
                            // 休眠5秒
                            try {
                                Thread.sleep(5000);
                                continue;
                            } catch (InterruptedException interruptedException) {
                                // 直接停止当前线程
                                Thread.currentThread().interrupt();
                            }
                        }
                        // 如果超过最大重试次数，休眠5分钟
                        try {
                            logger.error("Http长轮询请求已超过最大重试次数，自动休眠5分钟。");
                            Thread.sleep(5 * 60 * 1000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

        }
    }
}
