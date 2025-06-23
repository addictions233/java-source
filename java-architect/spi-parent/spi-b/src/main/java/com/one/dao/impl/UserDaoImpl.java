package com.one.dao.impl;

import com.one.dao.IUserDao;

/**
 * @author one
 * @description spi接口实现类
 * @date 2024-1-27
 */
public class UserDaoImpl implements IUserDao {
    /**
     * spi需要被实现的方法
     */
    @Override
    public void saveUser() {
        System.out.println("spi-a中的UserDaoImpl实现类的saveUser方法");
    }
}

