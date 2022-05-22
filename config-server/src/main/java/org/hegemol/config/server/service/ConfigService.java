package org.hegemol.config.server.service;

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
     * @param app
     * @return 配置信息
     */
    String getConfig(String app);

    /**
     * 修改配置内容
     *
     * @param app     应用名
     * @param content 配置内容
     */
    void updateConfig(String app, String content);

    /**
     * 缓存所有数据
     *
     * @return 所有配置数据
     */
    List<ConfigDO> cacheAll();
}