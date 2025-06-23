package com.zhouyu.service;

import org.springframework.beans.factory.annotation.Value;

//@Component
public class UserService {


	@Value("${random.int}")
	private Integer path;

	public String test(){
		return "zhouyu";
	}

}
