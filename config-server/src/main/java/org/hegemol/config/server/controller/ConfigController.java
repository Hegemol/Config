package org.hegemol.config.server.controller;

import org.hegemol.config.common.model.Result;
import org.hegemol.config.server.handler.HttpLongPollingHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 配置监听处理器
 *
 * @author KevinClair
 **/
@RestController
public class ConfigController {

    private final HttpLongPollingHandler handler;

    public ConfigController(final HttpLongPollingHandler handler) {
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
    @PostMapping("/init")
    public Result<String> init(HttpServletRequest request) {
        return Result.success(handler.init(request));
    }
}
