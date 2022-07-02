package org.hegemol.config.client.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.hegemol.config.client.annodation.ChangeListener;
import org.hegemol.config.client.model.ChangeListenerModel;
import org.hegemol.config.common.model.ConfigChangeFieldModel;
import org.hegemol.config.common.utils.FieldReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 配置事件监听
 *
 * @author KevinClair
 **/
public class ConfigChangeListener implements ApplicationListener<ConfigChangeEvent>, BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeListener.class);
    private List<ChangeListenerModel> configListeners = new ArrayList();

    @Override
    public void onApplicationEvent(final ConfigChangeEvent event) {
        ConfigChangeManager listenerManager = ConfigChangeManager.getListenerManager(event.getGroup());
        if (Objects.isNull(listenerManager)) {
            return;
        }
        Map<String, ConfigChangeFieldModel> propertyListener = listenerManager.getPropertyChangeManagerMap();
        JSONObject configJsonObject = JSON.parseObject(event.getConfig());
        for (Map.Entry<String, ConfigChangeFieldModel> entry : propertyListener.entrySet()) {
            // 如果当前的配置key需要动态刷新，刷新field
            if (configJsonObject.containsKey(entry.getKey())) {
                refreshBeanField(entry.getValue(), configJsonObject.getString(entry.getKey()));
            }
        }
        // 调用配置监听方法
        for (ChangeListenerModel listener : configListeners) {
            if (listener.getGroup().equals(event.getGroup())) {
                try {
                    listener.getMethod().invoke(listener.getObject(), event.getConfig());
                } catch (Exception e) {
                    logger.error("配置监听失败", e);
                }
            }
        }
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            // 监听所有
            if (method.isAnnotationPresent(ChangeListener.class)) {
                ChangeListener listener = method.getAnnotation(ChangeListener.class);
                configListeners.add(new ChangeListenerModel(bean, method, listener.group()));
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
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
