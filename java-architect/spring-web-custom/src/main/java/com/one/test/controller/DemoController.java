package com.one.test.controller;

import com.one.mvcframework.annotation.Autowried;
import com.one.mvcframework.annotation.Controller;
import com.one.mvcframework.annotation.RequestMapping;
import com.one.mvcframework.annotation.RequestParam;
import com.one.test.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: DemoController
 * @Description: 手动实现spring mvc
 * @Author: one
 * @Date: 2022/04/26
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowried
    private DemoService demoService;

    @RequestMapping("/getName")
    public String getConcatName(HttpServletRequest req, HttpServletResponse resp, @RequestParam("name") String name) {
        return demoService.getConcatName(name);
    }
}
