package org.hegemol.config.client.config;

import com.alibaba.fastjson2.JSON;
import org.hegemol.config.client.annodation.ConfigValue;
import org.hegemol.config.client.event.ConfigChangeManager;
import org.hegemol.config.common.model.ConfigChangeFieldModel;
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
 * 配置初始化管理器
 *
 * @author KevinClair
 **/
public class ConfigInitManager implements InstantiationAwareBeanPostProcessor, BeanNameAware {

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

                    // 刷新field
                    ConfigChangeFieldModel configChangeFieldModel = new ConfigChangeFieldModel(bean, propertyName);
                    refreshBeanField(configChangeFieldModel, confValue);

                    // 如果当前配置key需要动态刷新，添加配置变更管理器
                    if (configValue.refresh()) {
                        ConfigChangeManager.addChangeField(configValue.group(), configValue.value(), configChangeFieldModel);
                    }
                }
            });
        }
        return true;
    }

    private void refreshBeanField(final ConfigChangeFieldModel configChangeFieldModel, final String confValue) {
        if (Objects.isNull(configChangeFieldModel.getBean())) {
            return;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(configChangeFieldModel.getBean());

        // property descriptor
        PropertyDescriptor propertyDescriptor = null;
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        if (propertyDescriptors != null && propertyDescriptors.length > 0) {
            for (PropertyDescriptor item : propertyDescriptors) {
                if (configChangeFieldModel.getPropertyName().equals(item.getName())) {
                    propertyDescriptor = item;
                }
            }
        }

        if (Objects.nonNull(propertyDescriptor) && Objects.nonNull(propertyDescriptor.getWriteMethod())) {
            beanWrapper.setPropertyValue(configChangeFieldModel.getPropertyName(), confValue);
        } else {
            final Object finalBean = configChangeFieldModel.getBean();
            ReflectionUtils.doWithFields(configChangeFieldModel.getBean().getClass(), fieldItem -> {
                if (configChangeFieldModel.getPropertyName().equals(fieldItem.getName())) {
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
