package com.one.factory.builder;

import com.one.Parser.XMLConfigParser;
import com.one.config.Configuration;
import com.one.factory.DefaultSqlSessionFactory;
import com.one.factory.SqlSessionFactory;

import java.io.InputStream;
import java.io.Reader;

/**
 * @ClassName: SqlSessionFactoryBuilder
 * @Description: 建造者设计模式:构造sqlSessionFactory工厂对象
 * @Author: one
 * @Date: 2022/03/16
 */
public class SqlSessionFactoryBuilder {
    /**
     * 使用字节流构建sqlSessionFactory对象
     * @param inputStream 读取配置文件获取的字节流
     * @return SqlSessionFactory
     */
    public SqlSessionFactory build(InputStream inputStream) {
        // 创建核心配置文件解析对象
        XMLConfigParser xmlConfigParser = new XMLConfigParser();
        // 读取并解析核心配置文件,获取configuration配置对象
        Configuration configuration = xmlConfigParser.parse(inputStream);
        return this.build(configuration);
    }

    /**
     * 使用字符流构建sqlSessionFactory对象
     * @param reader 字符流
     * @return SqlSessionFactory
     */
    public SqlSessionFactory build(Reader reader) {
        XMLConfigParser xmlConfigParser = new XMLConfigParser();
        Configuration configuration = xmlConfigParser.parse(reader);
        return this.build(configuration);
    }

    /**
     * 使用私有方法封装对内实现细节
     * @param configuration configuration
     * @return SqlSessionFactory sqlSession工厂对象
     */
    private SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
