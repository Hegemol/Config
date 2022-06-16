package org.hegemol.config.client.example;

import org.hegemol.config.client.annodation.ConfigValue;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author KevinClair
 **/
@Configuration
public class DemoConfig {

    @ConfigValue("testKey")
    private String testKey;

    /**
     * Gets the value of testKey.
     *
     * @return the value of testKey
     */
    public String getTestKey() {
        return testKey;
    }

    /**
     * Sets the testKey.
     *
     * @param testKey testKey
     */
    public void setTestKey(final String testKey) {
        this.testKey = testKey;
    }
}
