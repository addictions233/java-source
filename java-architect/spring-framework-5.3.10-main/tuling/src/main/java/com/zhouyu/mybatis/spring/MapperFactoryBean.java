package com.zhouyu.mybatis.spring;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author one
 * @description TODO
 * @date 2024-3-3
 */
//@Component
public class MapperFactoryBean<T> implements FactoryBean<T> {
	private Class<T> mapperInterface;

	private SqlSession sqlSession;

	public MapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public void setSqlSession(SqlSessionFactory sqlSessionFactory) {
		sqlSessionFactory.getConfiguration().addMapper(mapperInterface);
		this.sqlSession = sqlSessionFactory.openSession();
	}


	@Override
	public T getObject() throws Exception {
		return sqlSession.getMapper(mapperInterface);
	}

	@Override
	public Class<T> getObjectType() {
		return mapperInterface;
	}
}
