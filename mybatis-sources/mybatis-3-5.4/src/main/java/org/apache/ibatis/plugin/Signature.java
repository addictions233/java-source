/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
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
package org.apache.ibatis.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that indicate the method signature.
 *   该注解的作用是指出要拦截的方法的签名
 *
 * @see Intercepts
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Signature {
  /**
   * Returns the java type.
   *
   * @return the java type
   */
  Class<?> type();

  /**
   * Returns the method name.
   *
   * @return the method name
   */
  String method();

  /**
   * Returns java types for method argument.
   * @return java types for method argument
   */
  Class<?>[] args();
}
