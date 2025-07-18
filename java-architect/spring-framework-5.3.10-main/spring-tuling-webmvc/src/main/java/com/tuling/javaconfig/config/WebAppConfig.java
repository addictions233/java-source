package com.tuling.javaconfig.config;

import com.tuling.javaconfig.interceptor.TulingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
* @vlog: 高于生活，源于生活
* @desc: 类的描述 web子容器
* @author: xsls
* @createDate: 2019/7/31 20:22
* @version: 1.0
*/
@Configuration
@ComponentScan(basePackages = {"com.tuling"},includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION,value = {RestController.class, Controller.class})
},useDefaultFilters =false)
@EnableWebMvc   // = <mvc:annotation-driven/>
public class WebAppConfig implements WebMvcConfigurer{

	/**
	 * 配置拦截器
	 * @return
	 */
	@Bean
	public TulingInterceptor tulingInterceptor() {
		return new TulingInterceptor();
	}

	/**
	 * 文件上传下载的组件
	 * @return
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setDefaultEncoding("UTF-8");
		multipartResolver.setMaxUploadSize(1024*1024*10);
		return multipartResolver;
	}

	/**
	 * 注册处理国际化资源的组件
	 * @return
	 */
/*	@Bean
	public AcceptHeaderLocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
		return acceptHeaderLocaleResolver;
	}*/

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tulingInterceptor()).addPathPatterns("/*");
	}


	/**
	 * 方法实现说明:配置试图解析器
	 * @author:xsls
	 * @exception:
	 * @date:2019/8/6 16:23
	 */
	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setSuffix(".jsp");
		viewResolver.setPrefix("/WEB-INF/jsp/");
		return viewResolver;
	}


/*
 不需要啊
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
	}*/

}
