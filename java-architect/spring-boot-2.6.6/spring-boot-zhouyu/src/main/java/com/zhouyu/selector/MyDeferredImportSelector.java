package com.zhouyu.selector;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author one
 * @description 测试DeferredImportSelector的使用: 相比于ImportSelector, 它会延迟解析
 * @date 2024-3-24
 */
public class MyDeferredImportSelector implements DeferredImportSelector {

	/**
	 * 在springboot中,如果#getImportGroup方法返回了值, 此方法是不会执行的
	 * @param importingClassMetadata
	 * @return
	 */
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		System.out.println("selectImports方法执行了...");
		return new String[]{"com.zhouyu.selector.UserEntityConfiguration"};
	}

	@Override
	public Class<? extends Group> getImportGroup() {
		System.out.println("getImportGroup方法执行了..");
		return MyGroup.class;
	}

	public static class MyGroup implements Group {

		private List<Entry> entryList = new ArrayList<>();

		/**
		 * 在springboot中, 对于DeferredImportSelector如果#getImportGroup方法返回了值其实是执行的#process()方法
		 * @param metadata
		 * @param selector
		 */
		@Override
		public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
			System.out.println("myGroup process.....");
			entryList.add(new Entry(metadata, new String("com.zhouyu.selector.UserEntityConfiguration")));
		}

		/**
		 * 在springboot中执行#selectImports方法
		 * @return
		 */
		@Override
		public Iterable<Entry> selectImports() {
			System.out.println("myGroup selectImports....");
			return entryList;
		}
	}
}
