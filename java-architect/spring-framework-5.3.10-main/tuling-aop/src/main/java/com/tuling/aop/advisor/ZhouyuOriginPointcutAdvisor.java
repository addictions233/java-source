package com.tuling.aop.advisor;

import com.tuling.UserService;
import com.tuling.aop.advice.ZhouyuBeforeAdvice;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author one
 * @description 自定义切面
 * @date 2023-7-16
 */
public class ZhouyuOriginPointcutAdvisor implements PointcutAdvisor {
	@Override
	public Advice getAdvice() {
		return new ZhouyuBeforeAdvice();
	}

	@Override
	public boolean isPerInstance() {
		return false;
	}

	/**
	 * 定义最原始的切入点
	 * @return Pointcut
	 */
	@Override
	public Pointcut getPointcut() {
		return new Pointcut() {
			/***
			 * 类过滤器
			 * @return
			 */
			@Override
			public ClassFilter getClassFilter() {
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						// 只有UserService类才匹配
						return clazz.equals(UserService.class);
					}
				};
			}

			@Override
			public MethodMatcher getMethodMatcher() {
				return new MethodMatcher() {
					@Override
					public boolean matches(Method method, Class<?> targetClass) {
						// 只有test方法才匹配
						return method.getName().equals("test2");
					}

					/**
					 * 是否运行时匹配逻辑, 如果返回true,可以在连接点方法joint point
					 * 执行时, 根据方法参数动态决定是否执行advice
					 * @return
					 */
					@Override
					public boolean isRuntime() {
						return true;
					}

					/**
					 * 当isRuntime()方法返回为true时,才会调用本方法判断
					 * 当前条件是否满足通知的代理逻辑
					 *
					 * @param method the candidate method
					 * @param targetClass the target class
					 * @param args arguments to the method
					 * @return
					 */
					@Override
					public boolean matches(Method method, Class<?> targetClass, Object... args) {
						List<Object> argsList = Arrays.stream(args).collect(Collectors.toList());
						return argsList.size() == 1 && argsList.get(0).equals("aaa");
					}
				};
			}
		};
	}
}
