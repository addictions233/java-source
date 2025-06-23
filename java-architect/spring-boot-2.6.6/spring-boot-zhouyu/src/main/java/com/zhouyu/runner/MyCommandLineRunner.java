package com.zhouyu.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author one
 * @description 可以获取所有的命令行参数, 然后对命令行参数进行操作
 * @date 2024-9-8
 */
@Component
public class MyCommandLineRunner implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
		System.out.println("-----------------MyCommandLineRunner----------------------");
		for (String arg : args) {
			System.out.println(arg);
		}
	}
}
