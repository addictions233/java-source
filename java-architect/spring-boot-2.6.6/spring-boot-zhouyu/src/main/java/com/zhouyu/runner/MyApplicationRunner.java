package com.zhouyu.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author one
 * @description TODO
 * @date 2024-9-8
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {
	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<String> nonOptionArgs = args.getNonOptionArgs();
		System.out.println("-----------------MyApplicationRunner-----------");
		for (String nonOptionArg : nonOptionArgs) {
			System.out.println(nonOptionArg);
		}
		System.out.println("--------------sourceArgs------------------");
		String[] sourceArgs = args.getSourceArgs();
		for (String sourceArg : sourceArgs) {
			System.out.println(sourceArg);
		}
	}
}
