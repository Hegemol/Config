package org.hegemol.config.server.handler;

import org.hegemol.config.common.model.ConfigDto;
import org.hegemol.config.common.utils.WorkThreadFactory;
import org.hegemol.config.server.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 长轮询处理器
 *
 * @author KevinClair
 **/
@Service
public class HttpLongPollingHandler {

    private final BlockingQueue<LongPollingClient> clients = new ArrayBlockingQueue<>(1024);

    private final ConfigService configService;

    private final ScheduledExecutorService scheduler;

    public HttpLongPollingHandler(final ConfigService configService) {
        this.configService = configService;
        this.scheduler = new ScheduledThreadPoolExecutor(1, new WorkThreadFactory("server-config-listener"));
    }

    /**
     * 配置监听
     *
     * @param dto 请求配置
     * @return
     */
    public String listener(ConfigDto dto) {
        return "";
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
            this.future = scheduler.schedule(
                    () -> {
                        // 移除当前客户端，此动作会在没有配置发生变更是触发
                        clients.remove(LongPollingClient.this);

                    }
                    , timeout, TimeUnit.MILLISECONDS);
        }
    }
}
