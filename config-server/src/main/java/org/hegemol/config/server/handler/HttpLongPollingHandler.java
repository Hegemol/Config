package org.hegemol.config.server.handler;

import com.alibaba.fastjson2.JSON;
import org.hegemol.config.common.model.LocalCacheServerData;
import org.hegemol.config.common.model.Result;
import org.hegemol.config.common.utils.WorkThreadFactory;
import org.hegemol.config.server.model.ConfigDO;
import org.hegemol.config.server.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
public class HttpLongPollingHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpLongPollingHandler.class);

    private final BlockingQueue<LongPollingClient> clients = new ArrayBlockingQueue<>(1024);

    private final ConfigService configService;

    private final ScheduledExecutorService scheduler;

    public HttpLongPollingHandler(final ConfigService configService) {
        this.configService = configService;
        this.scheduler = new ScheduledThreadPoolExecutor(1, new WorkThreadFactory("server-config-listener"));
        List<ConfigDO> configDOS = configService.cacheAll();
        LocalCacheServerData.getInstance().setData(configDOS.stream().collect(Collectors.toMap(ConfigDO::getApp, ConfigDO::getContent)));
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
        scheduler.execute(new LongPollingClient(asyncContext, 60));
    }

    /**
     * 初始化客户端的配置信息
     *
     * @param request 请求
     * @return 当前应用的配置数据
     */
    public String init(HttpServletRequest request) {
        return configService.getConfig(request.getParameter("app"));
    }

    private void generateResponse(HttpServletResponse response, String config) {
        try {
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-cache,no-store");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(JSON.toJSONString(Result.success(config)));
        } catch (IOException ex) {
            log.error("Http long polling send response error.", ex);
        }
    }

    class LongPollingClient implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(LongPollingClient.class);

        // 异步处理
        private final AsyncContext asyncContext;

        // 定时延迟时间
        private final long timeout;

        // 定时任务执行返回
        private Future<?> future;

        public LongPollingClient(final AsyncContext asyncContext, final long timeout) {
            this.asyncContext = asyncContext;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                this.future = scheduler.schedule(
                        () -> {
                            // 移除当前客户端，此动作会在没有配置发生变更是触发
                            clients.remove(LongPollingClient.this);
                            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
                            // 通过请求的参数获取服务端的配置，之后返回；
                            String serverConfig = LocalCacheServerData.getInstance().getData().get(request.getParameter("app"));
                            response(serverConfig);
                        }
                        , timeout, TimeUnit.SECONDS);
                clients.add(this);
            } catch (Exception exception) {
                logger.error("Http long polling client execute error.", exception);
            }
        }

        /**
         * 向客户端响应结果
         *
         * @param config 配置信息
         */
        private void response(String config) {
            if (Objects.nonNull(future)) {
                // 如果此时future不为null，那么就是当前的定时任务调度已开启，但未执行，所以取消任务
                future.cancel(false);
            }
            // 响应结果
            generateResponse((HttpServletResponse) asyncContext.getResponse(), config);
            // 完成异步任务
            asyncContext.complete();
        }
    }
}
