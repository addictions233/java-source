package com.one.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author one
 * @TableName user
 */
@Data  // get,set方法, equals, hashcode, toString方法
public class User {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 姓名
     */
    private String username;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 0表示男，1表示女，2表示未知
     */
    private String sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 住址
     */
    private String address;

}