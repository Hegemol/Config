package org.hegemol.config.client.model;

import java.lang.reflect.Method;

/**
 * 配置监听模型
 *
 * @author KevinClair
 **/
public class ChangeListenerModel {

    private Object object;

    private Method method;

    private String group;

    public ChangeListenerModel() {
    }

    public ChangeListenerModel(final Object object, final Method method, final String group) {
        this.object = object;
        this.method = method;
        this.group = group;
    }

    /**
     * Gets the value of object.
     *
     * @return the value of object
     */
    public Object getObject() {
        return object;
    }

    /**
     * Sets the object.
     *
     * @param object object
     */
    public void setObject(final Object object) {
        this.object = object;
    }

    /**
     * Gets the value of method.
     *
     * @return the value of method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the method.
     *
     * @param method method
     */
    public void setMethod(final Method method) {
        this.method = method;
    }

    /**
     * Gets the value of group.
     *
     * @return the value of group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group group
     */
    public void setGroup(final String group) {
        this.group = group;
    }
}
