package org.hegemol.config.client;

import org.hegemol.config.common.model.ConfigChangeFieldModel;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

class ConfigClientApplicationTests {

    @Test
    public void contextLoads() {
        ConfigChangeFieldModel configChangeFieldModel = new ConfigChangeFieldModel();
        configChangeFieldModel.setPropertyName("1111111");
        ReflectionUtils.doWithFields(configChangeFieldModel.getClass(), fieldItem -> {
            if (fieldItem.getName().equals("propertyName")) {
                fieldItem.setAccessible(true);
                fieldItem.set(configChangeFieldModel, "222222222");
            }
        });


        System.out.println(configChangeFieldModel.getPropertyName());

        ConfigChangeFieldModel configChangeFieldModel2 = new ConfigChangeFieldModel();
        System.out.println(configChangeFieldModel2.getPropertyName());

    }

}
