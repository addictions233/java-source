package com.one.controller;

import com.one.service.UseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author one
 * @description controller层
 * @date 2024-2-11
 */
@RestController
public class UseController {
    @Autowired
    private UseService useService;

    @GetMapping("/test")
    public String test() {
        return useService.test();
    }

}
