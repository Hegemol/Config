package org.hegemol.config.server.service.impl;

import org.hegemol.config.server.mapper.ConfigMapper;
import org.hegemol.config.server.model.ConfigDO;
import org.hegemol.config.server.service.ConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置服务实现
 *
 * @author KevinClair
 **/
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper mapper;

    public ConfigServiceImpl(final ConfigMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String getConfig(final String app) {
        return mapper.getConfig(app);
    }

    @Override
    public void updateConfig(final String app, final String content) {

    }

    @Override
    public List<ConfigDO> cacheAll() {
        return mapper.getAll();
    }
}
