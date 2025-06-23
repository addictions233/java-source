package com.one.pojo;

/**
 * notes: 性别枚举类
 *
 * @author one
 */
public enum SexEnum {
    // 男
    MAN(0, "男"),
    // 女
    WOMEN(1, "女"),
    // 未知
    UNKNOWN(2, "未知");

    private Integer value;

    private String name;

    /**
     * 构造器
     * @param value 值
     * @param name 名称
     */
    SexEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static String getName(Integer value) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.value.equals(value)) {
                return sexEnum.name;
            }
        }
        return null;
    }
}
