package com.one.test.service.impl;

import com.one.mvcframework.annotation.Service;
import com.one.test.service.DemoService;

/**
 * @ClassName: ServiceImpl
 * @Description: TODO
 * @Author: one
 * @Date: 2022/04/26
 */
@Service
public class DemoServiceImpl implements DemoService {
    /**
     * 方法
     *
     * @param name 入参
     * @return 结果
     */
    @Override
    public String getConcatName(String name) {
        return "My Name is " + name + "_aaa";
    }
}
