package com.zhouyu;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * @author one
 * @description 自定义Condition, 对是否创建bean对象进行条件控制
 * 				配合@Conditional注解使用
 * @date 2023-2-25
 */
public class UserCondition implements Condition {
	/**
	 * Determine if the condition matches.
	 *
	 * @param context  the condition context
	 * @param metadata the metadata of the {@link AnnotationMetadata class}
	 *                 or {@link MethodMetadata method} being checked
	 * @return {@code true} if the condition matches and the component can be registered,
	 * or {@code false} to veto the annotated component's registration
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		try {
			context.getClassLoader().loadClass("com.zhouyu.service.User");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
