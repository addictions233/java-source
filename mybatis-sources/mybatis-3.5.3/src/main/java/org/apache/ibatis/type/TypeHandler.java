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
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

  /**
   * 负责将Java类型转换为JDBC类型, 本质上执行的就是JDBC中的PreparedStatement的set方法
   *  例如: String sql = "insert into user(name, age) values(?, ?)";
   *       PreparedStatement ps = conn.prepareStatement(sql);
   *       ps.setString(1, "张三");
   *       ps.setInt(2, 18);
   * @param ps
   * @param i 对应占位符的位置
   * @param parameter 占位符对应的值
   * @param jdbcType 对应的数据库类型
   */
  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
   * 从ResultSet中获取数据时会调用此方法,会将数据从JdbcType转换为Java类型,本质上执行的就是JDBC中的ResultSet的get方法
   *  例如: ResultSet rs = ps.executeQuery();
   *       String name = rs.getString("name");
   *       int age = rs.getInt("age");
   */
  T getResult(ResultSet rs, String columnName) throws SQLException;

  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
