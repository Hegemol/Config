package org.hegemol.config.client.example;

import org.hegemol.config.client.annodation.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author KevinClair
 **/
@Component
public class DemoChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(DemoChangeListener.class);

    @ChangeListener
    public void changeListener(String config) {
        logger.info("监听到配置更新：{}", config);
    }
}
