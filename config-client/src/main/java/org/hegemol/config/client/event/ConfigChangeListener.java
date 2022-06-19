package org.hegemol.config.client.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.hegemol.config.common.model.ConfigChangeFieldModel;
import org.hegemol.config.common.utils.FieldReflectionUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;

/**
 * 配置事件监听
 *
 * @author KevinClair
 **/
public class ConfigChangeListener implements ApplicationListener<ConfigChangeEvent> {

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
