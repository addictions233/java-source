package com.zhouyu;

import com.zhouyu.service.User;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author one
 * @description 学习spring前置知识的使用
 * @date 2023-1-19
 */
public class Main {
	public static void main(String[] args) throws IOException {
		// 获取运行时环境变量的功能
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
		Map<String, Object> systemEnvironment = applicationContext.getEnvironment().getSystemEnvironment();
		System.out.println(systemEnvironment);

		System.out.println("==================");

		Map<String, Object> systemProperties = applicationContext.getEnvironment().getSystemProperties();
		System.out.println(systemProperties);

		System.out.println("==================");
		MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
		System.out.println(propertySources);

		System.out.println("==========");
		String property = applicationContext.getEnvironment().getProperty("zhouyu");  // 用@PropertySource注解指定配置文件的路径
		System.out.println(property);

		// 事件发布的功能
		applicationContext.publishEvent("122332");

		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToUserConverter());
		User user1 = conversionService.convert("123", User.class);
		System.out.println(user1);

		// 通用的转换器,将某个对象转换为另外一个对象
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();
		typeConverter.registerCustomEditor(User.class, new StringToUserPropertyEditor());
		typeConverter.setConversionService(conversionService);
		User user2 = typeConverter.convertIfNecessary("1234", User.class);
		System.out.println(user2);

		// 对创建bean对象的顺序进行排序
		OrderComparator comparator = new OrderComparator();
		List<User> list = new ArrayList<>();
		list.add(user1);
		list.add(user2);
		System.out.println("=========排序前=========");
		System.out.println(list);
		System.out.println("==========排序后=========");
		list.sort(comparator);
		System.out.println(list);

		// 元数据读取器, 底层使用ASM解析字节码数据
		SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
		MetadataReader metadataReader = metadataReaderFactory.getMetadataReader("com.zhouyu.service.UserService");
		// 类元数据: 获取一个类的类名,接口,内部类等信息
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		System.out.println(classMetadata.getClassName());
		System.out.println(classMetadata.getInterfaceNames()[0]);

		// 注解元数据: 获取注解的信息
		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
		System.out.println(annotationMetadata.hasAnnotation(Component.class.getName()));
		System.out.println(annotationMetadata.hasMetaAnnotation(Component.class.getName()));
		System.out.println(annotationTypes);
	}
}
