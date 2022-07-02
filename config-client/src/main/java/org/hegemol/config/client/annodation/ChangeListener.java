package org.hegemol.config.client.annodation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置监听注解
 *
 * @author KevinClair
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChangeListener {

    /**
     * 配置项的组
     *
     * @return
     */
    String group() default "DEFAULT_GROUP";
}
