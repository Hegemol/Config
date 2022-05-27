package org.hegemol.config.server.handler;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.hegemol.config.common.model.ConfigDTO;
import org.hegemol.config.common.model.ConfigResponse;
import org.hegemol.config.common.model.LocalCacheServerData;
import org.hegemol.config.common.model.Md5Config;
import org.hegemol.config.common.model.Result;
import org.hegemol.config.common.utils.Md5Utils;
import org.hegemol.config.common.utils.WorkThreadFactory;
import org.hegemol.config.server.model.ConfigDO;
import org.hegemol.config.server.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 长轮询处理器
 *
 * @author KevinClair
 **/
@Service
public class HttpLongPollingHandler implements ApplicationListener<DataChangeEvent> {

    private static final Logger log = LoggerFactory.getLogger(HttpLongPollingHandler.class);

    private static final Map<String, BlockingQueue<LongPollingClient>> clients = new HashMap<>();

    private final ConfigService configService;

    private final ScheduledExecutorService scheduler;

    public HttpLongPollingHandler(final ConfigService configService) {
        this.configService = configService;
        this.scheduler = new ScheduledThreadPoolExecutor(1, new WorkThreadFactory("server-config-listener"));
        List<ConfigDO> configDOS = configService.cacheAll();
        // key为app，value为对应app的所有配置数据
        Map<String, List<ConfigDO>> collect = configDOS.stream().collect(Collectors.toMap(ConfigDO::getApp, value -> new ArrayList<ConfigDO>() {{
            add(value);
        }}, (o1, o2) -> {
            o2.addAll(o1);
            return o2;
        }));
        Map<String, Map<String, String>> cacheMap = new HashMap<>(collect.size());
        collect.forEach((k, v) -> {
            cacheMap.put(k, v.stream().collect(Collectors.toMap(ConfigDO::getGroup, ConfigDO::getConfig)));
        });
        LocalCacheServerData.getInstance().setData(cacheMap);
    }

    /**
     * 配置监听
     *
     * @param request 请求配置
     * @return
     */
    public void listener(HttpServletRequest request) {
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(0L);
        scheduler.execute(new LongPollingClient(asyncContext, 60, this.getRemoteIp(request), request.getParameter("app")));
    }

    /**
     * 根据app和对应的组，获取对用的配置数据
     *
     * @param request 请求体，参数包含app和group
     * @return 对应app所包含分组的配置信息
     */
    public List<ConfigResponse> getConfig(final HttpServletRequest request) {
        return configService.getConfig(request.getParameter("app"), Arrays.asList(request.getParameterValues("group")));
    }

    private void generateResponse(HttpServletResponse response, List<String> changeGroup) {
        try {
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-cache,no-store");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(JSON.toJSONString(Result.success(changeGroup)));
        } catch (IOException ex) {
            log.error("Http long polling send response error.", ex);
        }
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(final DataChangeEvent event) {
        ConfigDTO source = event.getSource();

        // 更新本地缓存
        LocalCacheServerData.getInstance().getData().get(source.getApp()).put(source.getGroup(), source.getConfig());
        // 响应所有客户端
        scheduler.execute(new DataChangeTask(source.getApp(), source.getGroup()));
    }

    /**
     * 获取远程ip地址
     *
     * @param request 请求
     * @return ip地址
     */
    private String getRemoteIp(final HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String header = request.getHeader("X-Real-IP");
        return StringUtils.isBlank(header) ? request.getRemoteAddr() : header;
    }


    class LongPollingClient implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(LongPollingClient.class);

        // 异步处理
        private AsyncContext asyncContext;

        // 定时延迟时间
        private long timeout;

        // 应用
        private String app;

        // 客户端地址
        private String ip;

        // 定时任务执行返回
        private Future<?> future;

        public LongPollingClient(final AsyncContext asyncContext, final long timeout, final String ip, final String app) {
            this.asyncContext = asyncContext;
            this.timeout = timeout;
            this.ip = ip;
            this.app = app;
        }

        @Override
        public void run() {
            try {
                this.future = scheduler.schedule(
                        () -> {
                            // 移除当前应用内的客户端，此动作会在没有配置发生变更是触发
                            clients.get(app).remove(LongPollingClient.this);
                            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
                            // 通过请求的参数获取服务端的配置
                            String app = request.getParameter("app");
                            Map<String, String> appConfigCache = LocalCacheServerData.getInstance().getData().get(app);
                            // 获取所有配置group和对应配置group的config的Md5值
                            List<Md5Config> clientConfigList = JSON.parseArray(request.getParameter("config"), Md5Config.class);

                            // 过滤出，对应group组的Md5和服务端不一致的，说明此时需要变更客户端的配置数据
                            List<String> changeGroup = clientConfigList.stream().filter(
                                    each ->
                                            !StringUtils.equals(each.getMd5Config(), Md5Utils.md5(appConfigCache.get(each.getGroup())))
                            ).map(Md5Config::getGroup).collect(Collectors.toList());

                            logger.info("客户端:{},应用:{}的配置组:{}，发生了配置变更", ip, app, JSON.toJSONString(changeGroup));
                            response(changeGroup);
                        }
                        , timeout, TimeUnit.SECONDS);
                if (Objects.isNull(clients.get(app))) {
                    // 初始化队列
                    BlockingQueue<LongPollingClient> client = new ArrayBlockingQueue<>(10);
                    client.add(this);
                    clients.put(app, client);
                    return;
                }
                clients.get(app).add(this);
            } catch (Exception exception) {
                logger.error("Http long polling client execute error.", exception);
            }
        }

        /**
         * 向客户端响应结果
         *
         * @param changeGroup 发生变更的配置组
         */
        private void response(List<String> changeGroup) {
            if (Objects.nonNull(future)) {
                // 如果此时future不为null，那么就是当前的定时任务调度已开启，但未执行，所以取消任务
                future.cancel(false);
            }
            // 响应结果
            generateResponse((HttpServletResponse) asyncContext.getResponse(), changeGroup);
            // 完成异步任务
            asyncContext.complete();
        }
    }

    class DataChangeTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(DataChangeTask.class);

        private String app;

        private String group;

        public DataChangeTask(final String app, final String group) {
            this.app = app;
            this.group = group;
        }

        @Override
        public void run() {
            if (!CollectionUtils.isEmpty(clients)) {
                // 根据应用名获取所有的客户端连接
                BlockingQueue<LongPollingClient> client = clients.get(app);
                if (!CollectionUtils.isEmpty(client)) {
                    // 取出所有客户端
                    List<LongPollingClient> clientList = new ArrayList<>(client.size());
                    client.drainTo(clientList);
                    Iterator<LongPollingClient> iterator = clientList.iterator();
                    while (iterator.hasNext()) {
                        LongPollingClient next = iterator.next();
                        iterator.remove();
                        next.response(Collections.singletonList(group));
                        logger.info("Http long polling，配置发生变更，主动响应客户端，ip:{}，app:{}，group:{}", next.ip, app, group);
                    }
                }
            }
        }
    }
}
