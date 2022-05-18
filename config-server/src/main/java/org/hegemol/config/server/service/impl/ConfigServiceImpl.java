package org.hegemol.config.server.service.impl;

import org.hegemol.config.server.service.ConfigService;
import org.springframework.stereotype.Service;

/**
 * 配置服务实现
 *
 * @author KevinClair
 **/
@Service
public class ConfigServiceImpl implements ConfigService {


    @Override
    public String getConfig(final String app) {
        return null;
    }

    @Override
    public void updateConfig(final String app, final String content) {

    }
}
