package com.one.dao;

/**
 * @author one
 * @description spi中定义规范的接口
 * @date 2024-1-27
 */
public interface IUserDao {
    /**
     * spi需要被实现的方法
     */
    void saveUser();
}
