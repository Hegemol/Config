package org.hegemol.config.common.model;

/**
 * @author KevinClair
 **/
public class ConfigChangeFieldModel {

    /**
     * bean对象
     */
    private Object bean;

    /**
     * 属性名
     */
    private String propertyName;

    public ConfigChangeFieldModel() {
    }

    public ConfigChangeFieldModel(final Object bean, final String propertyName) {
        this.bean = bean;
        this.propertyName = propertyName;
    }

    /**
     * Gets the value of bean.
     *
     * @return the value of bean
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Sets the bean.
     *
     * @param bean bean
     */
    public void setBean(final Object bean) {
        this.bean = bean;
    }

    /**
     * Gets the value of propertyName.
     *
     * @return the value of propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the propertyName.
     *
     * @param propertyName propertyName
     */
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
}
