package com.one.test;

import com.one.factory.SqlSessionFactory;
import com.one.factory.builder.SqlSessionFactoryBuilder;
import com.one.framework.sqlSession.SqlSession;
import com.one.io.Resources;
import com.one.pojo.User;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MybatisV3
 * @Description: mybatis V3版本:
 *      1, 使用建造者设计模式对 v2版本的代码进行抽取封装
 *      2, 将实现功能所用到的各个方法抽取到对应的对象中,按照类对象进行职责分工
 * @Author: one
 * @Date: 2022/03/16
 */
public class MybatisV3 {
    /**
     * 因为sqlSessionFactory不用每次都构建,所以抽取为成员变量
     */
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 加载mybatis核心配置文件
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        // 构建sqlSessionFactory工厂对象
        this.sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
    }

    @Test
    public void testMybatisV3() {
        // 使用sqlSession对象完成增删改查
        // 1,使用sqlSessionFactory构建sqlSession对象,每次执行sql语句都要构建sqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 2,构造查询参数
        Map<String, Object> param = new HashMap<String, Object>() {
            {
                put("username","赵雄");
                put("age",29);
            }
        };

        // 3,使用sqlSession执行sql语句,获取结果
        User user = sqlSession.selectOne("test.queryUser", param);
        // 4,打印结果
        System.out.println(user);
    }
}
