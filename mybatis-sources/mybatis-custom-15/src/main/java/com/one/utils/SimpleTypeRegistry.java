package com.one.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName: SimpleTypeRegistry
 * @Description: 判断一个类是否是简单类型
 * @Author: one
 * @Date: 2022/02/20
 */
public class SimpleTypeRegistry {
    private static final Set<Class> SIMPLE_TYPE_SET;

    static {
        SIMPLE_TYPE_SET = new HashSet<>();
        SIMPLE_TYPE_SET.add(String.class);
        SIMPLE_TYPE_SET.add(Date.class);
        SIMPLE_TYPE_SET.add(BigInteger.class);
        SIMPLE_TYPE_SET.add(BigDecimal.class);
        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(Boolean.class);
    }

    public static boolean isSimpleType(Class<?> clazz) { //知道是哪种类型（父类），就用T行了,如果完全不知道的就用？
        return SIMPLE_TYPE_SET.contains(clazz);
    }

    public static Class<?> getClassByName(String typeName) {
        try {
            Class<?> typeClass = Class.forName(typeName);
            return typeClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
