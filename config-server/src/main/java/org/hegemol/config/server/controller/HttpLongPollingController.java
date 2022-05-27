package org.hegemol.config.server.controller;

import org.hegemol.config.common.model.ConfigResponse;
import org.hegemol.config.common.model.Result;
import org.hegemol.config.server.handler.HttpLongPollingHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配置监听处理器
 *
 * @author KevinClair
 **/
@RestController
public class HttpLongPollingController {

    private final HttpLongPollingHandler handler;

    public HttpLongPollingController(final HttpLongPollingHandler handler) {
        this.handler = handler;
    }

    /**
     * 配置监听
     *
     * @param request 请求对象
     */
    @PostMapping("/listener")
    public void listener(HttpServletRequest request) {
        handler.listener(request);
    }

    /**
     * 初始化配置数据
     *
     * @param request 请求对象
     */
    @PostMapping("/get")
    public Result<List<ConfigResponse>> getConfig(HttpServletRequest request) {
        return Result.success(handler.getConfig(request));
    }
}
