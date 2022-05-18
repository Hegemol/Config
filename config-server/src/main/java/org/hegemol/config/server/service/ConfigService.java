package org.hegemol.config.server.service;

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
}