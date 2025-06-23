package com.zhouyu.mybatis.mapper;

import org.apache.ibatis.annotations.Select;

/**
 * @author one
 * @description TODO
 * @date 2024-3-3
 */
public interface UserMapper {

	@Select("select 'test'")
	String selectById();
}
