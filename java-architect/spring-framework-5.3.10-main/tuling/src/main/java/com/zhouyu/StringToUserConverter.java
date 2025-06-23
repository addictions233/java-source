package com.zhouyu;

import com.zhouyu.service.User;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Set;

/**
 * @author one
 * @description 将配置文件中的字符串转换为bean对象的属性值
 * @date 2023-1-20
 */
public class StringToUserConverter implements ConditionalGenericConverter {
	/**
	 * Should the conversion from {@code sourceType} to {@code targetType} currently under
	 * consideration be selected?
	 *
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return true if conversion should be performed, false otherwise
	 */
	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		// 满足sourType为String且 targetType为User才转换
		return sourceType.getType().equals(String.class) && targetType.getType().equals(User.class);
	}

	/**
	 * Return the source and target types that this converter can convert between.
	 * <p>Each entry is a convertible source-to-target type pair.
	 * <p>For {@link ConditionalConverter conditional converters} this method may return
	 * {@code null} to indicate all source-to-target pairs should be considered.
	 */
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, User.class));
	}

	/**
	 * Convert the source object to the targetType described by the {@code TypeDescriptor}.
	 *
	 * @param source     the source object to convert (may be {@code null})
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return the converted object
	 */
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		// 写转换规则
		User user = new User();
		user.setUsername((String) source);
		return user;
	}
}
