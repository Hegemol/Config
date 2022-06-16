package org.hegemol.config.client.annodation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置注解
 *
 * @author KevinClair
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigValue {

    /**
     * 配置项的组
     *
     * @return
     */
    String group() default "DEFAULT_GROUP";

    /**
     * 配置项的key
     *
     * @return
     */
    String value();

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";
}
