package org.hegemol.config.common.utils;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;

/**
 * Filed映射工具
 *
 * @author KevinClair
 **/
public class FieldReflectionUtil {

    /**
     * 解析value至对应的数据类型
     *
     * @param fieldType filed的类型
     * @param value     filed的值
     * @return 解析后的值
     */
    public static Object parseValue(Class<?> fieldType, String value) {
        if(value==null || value.trim().length()==0)
            return null;
        value = value.trim();

        if (String.class.equals(fieldType)) {
            return value;
        } else if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return parseBoolean(value);
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return parseShort(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return parseInt(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return parseLong(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return parseFloat(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return parseDouble(value);
        } else {
            throw new RuntimeException("illeagal conf data type, type=" + fieldType);
        }
    }
}
