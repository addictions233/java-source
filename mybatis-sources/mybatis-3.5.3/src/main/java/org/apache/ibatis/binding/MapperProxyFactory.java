/**
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;

/**
 * @author Lasse Voss
 */
public class MapperProxyFactory<T> {

  private final Class<T> mapperInterface;
  private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

  public MapperProxyFactory(Class<T> mapperInterface) {
    this.mapperInterface = mapperInterface;
  }

  public Class<T> getMapperInterface() {
    return mapperInterface;
  }

  public Map<Method, MapperMethod> getMethodCache() {
    return methodCache;
  }

  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
    /**
     * JDK动态代理
     * 1.我们的mapperInterface.getClassLoader() 类加载器
     * 2.我们的mapperInterface 我们的接口类型
     * 3.我们的mapperProxy 我们的代理对象
     */
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

  /**
   * 方法实现说明:创建我们的UserMapper接口代理
   * @author:xsls
   * @param sqlSession:sqlSessionTemplate
   * @return:
   * @exception:
   * @date:2019/8/22 20:46
   */
  public T newInstance(SqlSession sqlSession) {
    /**
     * 创建我们的代理对象
     */
    final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
    /**
     * 创建我们的Mapper代理对象返回
     */
    return newInstance(mapperProxy);
  }

}
