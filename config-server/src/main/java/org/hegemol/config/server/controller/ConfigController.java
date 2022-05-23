package org.hegemol.config.server.controller;

import org.hegemol.config.common.model.ConfigVO;
import org.hegemol.config.server.service.ConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置控制器
 *
 * @author KevinClair
 **/
@RestController
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(final ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/update")
    public void update(@RequestBody ConfigVO vo) {
        configService.updateConfig(vo);
    }
}
