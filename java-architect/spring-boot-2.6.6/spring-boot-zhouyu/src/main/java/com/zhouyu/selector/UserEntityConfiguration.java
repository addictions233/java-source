package com.zhouyu.selector;

import com.zhouyu.service.UserEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author one
 * @description TODO
 * @date 2024-9-8
 */
public class UserEntityConfiguration {

	@Bean
	@ConditionalOnMissingBean(UserEntity.class)
	public UserEntity userEntity() {
		return new UserEntity("userEntity2");
	}
}
