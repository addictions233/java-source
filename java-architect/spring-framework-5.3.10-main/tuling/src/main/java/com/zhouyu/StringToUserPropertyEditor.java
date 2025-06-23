package com.zhouyu;

import com.zhouyu.service.User;

import java.beans.PropertyEditorSupport;

/**
 * @author one
 * @description 类型转换器: 将属性值转换为对象
 * @date 2023-1-20
 */
public class StringToUserPropertyEditor extends PropertyEditorSupport {
	/**
	 * Sets the property value by parsing a given String.  May raise
	 * java.lang.IllegalArgumentException if either the String is
	 * badly formatted or if this kind of property can't be expressed
	 * as text.
	 *
	 * @param text The string to be parsed.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		User user = new User();
		user.setUsername(text);
		this.setValue(user);
	}
}
