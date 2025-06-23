package com.one.service.impl;

import com.one.service.UseService;
import org.springframework.stereotype.Service;

/**
 * @author one
 * @description service层实现
 * @date 2024-2-11
 */
@Service
public class UseServiceImpl implements UseService {
    @Override
    public String test() {
        return "test";
    }
}
