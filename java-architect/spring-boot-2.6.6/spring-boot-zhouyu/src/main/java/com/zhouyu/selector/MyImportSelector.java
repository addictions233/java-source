package com.zhouyu.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author one
 * @description TODO
 * @date 2024-9-8
 */
public class MyImportSelector implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{"com.zhouyu.selector.UserEntityConfiguration"};
	}
}
