package org.hegemol.config.client.config;

import com.alibaba.fastjson2.JSON;
import org.hegemol.config.client.annodation.ConfigValue;
import org.hegemol.config.common.model.LocalCacheClientData;
import org.hegemol.config.common.utils.FieldReflectionUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 * @author KevinClair
 **/
public class ConfigRefreshManager implements InstantiationAwareBeanPostProcessor, BeanNameAware {

    private String beanName;

    @Override
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        if (!beanName.equals(this.beanName)) {
            ReflectionUtils.doWithFields(bean.getClass(), field -> {
                if (field.isAnnotationPresent(ConfigValue.class)) {
                    String propertyName = field.getName();
                    ConfigValue configValue = field.getAnnotation(ConfigValue.class);

                    String groupValue = LocalCacheClientData.getInstance().getConfig().get(configValue.group());
                    Map configMap = JSON.parseObject(groupValue, Map.class);
                    String confValue = (String) configMap.getOrDefault(configValue.value(), configValue.defaultValue());

                    // resolves placeholders
                    refreshBeanField(propertyName, confValue, bean);
                }
            });
        }
        return true;
    }

    private void refreshBeanField(final String propertyName, final String confValue, final Object bean) {
        if (Objects.isNull(bean)) {
            return;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);

        // property descriptor
        PropertyDescriptor propertyDescriptor = null;
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        if (propertyDescriptors != null && propertyDescriptors.length > 0) {
            for (PropertyDescriptor item : propertyDescriptors) {
                if (propertyName.equals(item.getName())) {
                    propertyDescriptor = item;
                }
            }
        }

        if (Objects.nonNull(propertyDescriptor) && Objects.nonNull(propertyDescriptor.getWriteMethod())) {
            beanWrapper.setPropertyValue(propertyName, confValue);
        } else {
            final Object finalBean = bean;
            ReflectionUtils.doWithFields(bean.getClass(), fieldItem -> {
                if (propertyName.equals(fieldItem.getName())) {
                    try {
                        Object valueObj = FieldReflectionUtil.parseValue(fieldItem.getType(), confValue);
                        fieldItem.setAccessible(true);
                        fieldItem.set(finalBean, valueObj);
                    } catch (IllegalAccessException e) {
                        throw e;
                    }
                }
            });
        }
    }
}
