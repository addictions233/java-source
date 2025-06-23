package com.tuling;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Protocol;

import java.util.List;

/**
 * @author one 
 * 测试dubbo中的spi机制的使用
 */
public class SpiTest {
    public static void main(String[] args) {

//        // 使用spi加载所有的Protocol接口的实现
//        ExtensionLoader<Protocol> extensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
//        // 获取nam为http的protocol接口的实现
//        Protocol protocol = extensionLoader.getExtension("http");
//        System.out.println(protocol);

//        ExtensionLoader<Person> extensionLoader = ExtensionLoader.getExtensionLoader(Person.class);
//        Person person = extensionLoader.getExtension("black");

        // 调用#getAdaptiveExtension()方法获取自适应扩展点
        ExtensionLoader<Protocol> extensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol protocol = extensionLoader.getAdaptiveExtension();

//        // dubbo使用依赖注入借助dubbo自己定义的URL对象
//        URL url = new URL("x", "localhost", 8080);
//        // 如果在spi中car接口有多个实现, 那么怎么进行依赖注入呢?
//        // 需要用URL对象来指定具体的依赖注入的car实现的用哪一个? 这里指定用black
//        url = url.addParameter("car", "black");
//        System.out.println(person.getCar().getCarName(url));


//        ExtensionLoader<Filter> extensionLoader = ExtensionLoader.getExtensionLoader(Filter.class);
//        URL url = new URL("http://", "localhost", 8080);
//        url = url.addParameter("cache", "test");
//        List<Filter> activateExtensions = extensionLoader.getActivateExtension(url, new String[]{"validation"}, CommonConstants.CONSUMER);
//        for (Filter activateExtension : activateExtensions) {
//            System.out.println(activateExtension);
//        }

    }
}
