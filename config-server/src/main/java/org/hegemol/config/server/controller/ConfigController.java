package org.hegemol.config.server.controller;

import org.hegemol.config.common.model.ConfigDto;
import org.hegemol.config.common.model.Result;
import org.hegemol.config.server.handler.HttpLongPollingHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param dto 数据传输对象
     * @return 当前app的配置信息
     */
    @RequestMapping("/listener")
    public Result<String> listener(ConfigDto dto) {
        return Result.success(handler.listener(dto));
    }
}
