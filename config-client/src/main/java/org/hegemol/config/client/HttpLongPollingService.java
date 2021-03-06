package org.hegemol.config.client;

import com.alibaba.fastjson2.JSON;
import org.hegemol.config.client.config.HttpLongPollingConfigurationProperties;
import org.hegemol.config.client.event.ConfigChangeEvent;
import org.hegemol.config.common.constant.Constants;
import org.hegemol.config.common.model.ConfigResponse;
import org.hegemol.config.common.model.LocalCacheClientData;
import org.hegemol.config.common.model.Md5Config;
import org.hegemol.config.common.utils.Md5Utils;
import org.hegemol.config.common.utils.WorkThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 长轮询请求服务
 *
 * @author KevinClair
 **/
public class HttpLongPollingService implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpLongPollingService.class);

    /**
     * 状态标识，为true时代表请求正在运行
     */
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private final RestTemplate restTemplate;

    private final ExecutorService executor;

    private final String app;

    private final List<String> group;

    private final ApplicationEventPublisher applicationEventPublisher;

    public HttpLongPollingService(final RestTemplate restTemplate,
                                  final HttpLongPollingConfigurationProperties configurationProperties,
                                  final ApplicationEventPublisher applicationEventPublisher) {
        this.restTemplate = restTemplate;
        this.app = configurationProperties.getApp();
        this.group = StringUtils.hasText(configurationProperties.getGroup()) ? Arrays.asList(configurationProperties.getGroup().split(",")) : Collections.singletonList(Constants.DEFAULT_GROUP);
        List<String> urlList = Arrays.asList(configurationProperties.getUrl().split(","));
        this.executor = new ThreadPoolExecutor(urlList.size(), urlList.size(), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new WorkThreadFactory("client"), new ThreadPoolExecutor.AbortPolicy());
        this.start(urlList);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 开启轮询请求
     *
     * @param urlList 服务端列表
     */
    private void start(List<String> urlList) {
        // 先初始化本地的配置信息
        this.getConfig(urlList.get(0), group, true);
        if (RUNNING.compareAndSet(false, true)) {
            urlList.forEach(each -> executor.execute(new HttpLongPollingTask(each)));
        }
    }

    private void getConfig(String url, List<String> groups, Boolean isInit) {
        // 初始化请求参数
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>(8);
        // 当前的请求应用
        requestParam.put("app", Arrays.asList(app));
        // 当前应用监听的应用组
        requestParam.put("group", groups);

        // 拼装请求参数
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParam, headers);
        String configUrl = url + Constants.GET_CONFIG_URL;

        // 发送请求
        String json = this.restTemplate.postForEntity(configUrl, httpEntity, String.class).getBody();
        // 获取data数据
        String config = JSON.parseObject(json).getString("data");
        List<ConfigResponse> configList = JSON.parseArray(config, ConfigResponse.class);

        // 本地缓存
        LocalCacheClientData.getInstance().setConfig(configList.stream().collect(Collectors.toMap(ConfigResponse::getGroup, ConfigResponse::getConfig)));
        logger.info("Http长轮询客户端配置信息获取，配置数据:{}", config);
        if (!isInit) {
            // 发布配置变更事件
            configList.forEach(each -> applicationEventPublisher.publishEvent(new ConfigChangeEvent(each.getGroup(), each.getGroup(), each.getConfig())));
        }
    }

    /**
     * 发起长轮询请求
     *
     * @param url 请求的url地址
     */
    private void doLongPolling(String url) {
        // 获取本地缓存的数据
        Map<String, String> config = LocalCacheClientData.getInstance().getConfig();
        // 初始化请求参数
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>(8);
        // 当前的请求应用
        requestParam.put("app", Arrays.asList(app));
        // 初始map，数据源为config，value为md5后的值
        List<Md5Config> md5Configs = new ArrayList<>(config.size());
        config.forEach((k, v) -> md5Configs.add(new Md5Config(k, Md5Utils.md5(v))));
        // 放入请求体
        requestParam.put("config", Collections.singletonList(JSON.toJSONString(md5Configs)));

        // 拼装请求参数
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestParam, headers);
        String listenerUrl = url + Constants.SERVER_LISTENER_URL;

        // 发送请求
        String json = this.restTemplate.postForEntity(listenerUrl, httpEntity, String.class).getBody();
        // 获取data数据
        String data = JSON.parseObject(json).getString("data");
        logger.info("Http长轮询客户端接收到服务端返回:{}", data);

        // 获取当前有哪些组的数据发生了变更
        List<String> changeGroups = JSON.parseArray(data, String.class);

        if (!CollectionUtils.isEmpty(changeGroups)) {
            // 根据变更组，请求服务端重新获取数据
            this.getConfig(url, changeGroups, false);
        }
    }

    @Override
    public void destroy() throws Exception {
        RUNNING.compareAndSet(true, false);
        Optional.ofNullable(executor).ifPresent(ex -> ex.shutdownNow());
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
