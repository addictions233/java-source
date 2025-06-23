package com.tuling.aop.introduction;

import org.aspectj.lang.annotation.DeclareParents;

/**
 * @author 周瑜
 */
//@Aspect
//@Component
public class ZhouyuAspect {

	/**
	 * 使用Introduction技术, 为一个customService类强行实现customInterface接口,该接口的实现方法在DefaultCustomInterface中实现
	 */
	@DeclareParents(value = "com.tuling.aop.introduction.CustomService", defaultImpl = DefaultCustomInterface.class)
	private CustomInterface customInterface;

}
