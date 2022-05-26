package org.hegemol.config.server.service.impl;

import org.hegemol.config.common.model.ConfigDTO;
import org.hegemol.config.common.model.ConfigResponse;
import org.hegemol.config.common.model.ConfigVO;
import org.hegemol.config.server.handler.DataChangeEvent;
import org.hegemol.config.server.mapper.ConfigMapper;
import org.hegemol.config.server.model.ConfigDO;
import org.hegemol.config.server.service.ConfigService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置服务实现
 *
 * @author KevinClair
 **/
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    public ConfigServiceImpl(final ConfigMapper mapper, final ApplicationEventPublisher eventPublisher) {
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<ConfigResponse> getConfig(final String app, final List<String> group) {
        return mapper.getConfig(app, group).stream().map(each -> new ConfigResponse(each.getApp(), each.getConfig(), each.getGroup())).collect(Collectors.toList());
    }

    @Override
    public void updateConfig(ConfigVO vo) {
        mapper.updateConfig(vo.getApp(), vo.getConfig());
        // 发布更新事件
        eventPublisher.publishEvent(new DataChangeEvent(new ConfigDTO(vo.getApp(), vo.getConfig()), vo.getApp(), vo.getConfig()));
    }

    @Override
    public List<ConfigDO> cacheAll() {
        return mapper.getAll();
    }
}
