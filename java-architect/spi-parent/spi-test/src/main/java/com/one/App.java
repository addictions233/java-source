package com.one;

import com.one.dao.IUserDao;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author one
 * @description spi测试启动类
 * @date 2024-1-27
 */
public class App {

    public static void main(String[] args) {
        ServiceLoader<IUserDao> userDaos = ServiceLoader.load(IUserDao.class);
        Iterator<IUserDao> iterator = userDaos.iterator();
        while(iterator.hasNext()) {
            IUserDao userDao = iterator.next();
            userDao.saveUser();
        }
    }
}
