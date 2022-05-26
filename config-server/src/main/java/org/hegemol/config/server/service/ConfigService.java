package org.hegemol.config.server.service;

import org.hegemol.config.common.model.ConfigResponse;
import org.hegemol.config.common.model.ConfigVO;
import org.hegemol.config.server.model.ConfigDO;

import java.util.List;

/**
 * 配置服务
 *
 * @author KevinClair
 **/
public interface ConfigService {


    /**
     * 根据app获取对应的配置信息
     *
     * @param app   应用名
     * @param group 分组
     * @return 配置信息
     */
    List<ConfigResponse> getConfig(String app, List<String> group);

    /**
     * 修改配置内容
     *
     * @param vo
     */
    void updateConfig(ConfigVO vo);

    /**
     * 缓存所有数据
     *
     * @return 所有配置数据
     */
    List<ConfigDO> cacheAll();
}