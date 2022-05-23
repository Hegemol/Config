package org.hegemol.config.server.mapper;

import org.apache.ibatis.annotations.Param;
import org.hegemol.config.server.model.ConfigDO;

import java.util.List;

/**
 * TODO
 *
 * @author KevinClair
 **/
public interface ConfigMapper {

    /**
     * 获取所有数据
     *
     * @return 所有的配置数据
     */
    List<ConfigDO> getAll();

    /**
     * 根据app获取对应的配置数据
     *
     * @param app 应用名
     * @return 配置数据
     */
    String getConfig(@Param("app") String app);

    /**
     * 修改app对应的配置信息
     *
     * @param app    应用名
     * @param config 配置数据
     */
    void updateConfig(@Param("app") String app, @Param("config") String config);
}
