package com.spring;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * @author one
 * @description TODO
 * @date 2022-12-29
 */
public class MyApplicationContext {
    /**
     * 配置类
     */
    private Class configClass;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, Object> singleObjectMap = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new LinkedList<>();

    /**
     * spring容器启动加载Bean对象的简单实现
     * @param configClass 配置类的字节码对象
     */
    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 进行包扫描,获取需要创建Bean对象的字节码对象
        scanPackage(configClass);

        // 创建单例bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope())) {
                // 单例bean要在加载时就创建
                String beanName = entry.getKey();
                Object bean = createBean(beanName, beanDefinition);
                singleObjectMap.put(beanName, bean);
            }
        }
    }


    private void scanPackage(Class configClass) {
        // 读取扫描路径
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String scanPath = componentScan.value();
//            System.out.println(scanPath);
            // 用路径分隔符替换
            scanPath = scanPath.replace(".", "/");
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(scanPath);
            File dir = new File(resource.getFile());
            for (File file : dir.listFiles()) {
//                System.out.println(file.getAbsolutePath());
                if (!file.getName().endsWith(".class")) {  // 只读取字节码文件
                    continue;
                }
                String absolutePath = file.getAbsolutePath();
                String classPackage = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class")); // 包路径
                // 替换路径符号
                String className = classPackage.replace("\\", "."); // 包名
                // 使用类加载器加载字节码文件
                Class<?> clazz = null;
                try {
                    clazz = classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz.isAnnotationPresent(Component.class)) {
                    if (BeanPostProcessor.class.isAssignableFrom(clazz)) { // isAssignableFrom 用字节码来判断父类
                        try {
                            // 在创建Bean对象时会用beanPostProcessor, 所以扫描时在创建bean对象需要判断需要创建的bean对象是否是BeanPostProcessor
                            BeanPostProcessor beanPostProcessor = (BeanPostProcessor)clazz.getConstructor().newInstance();
                            beanPostProcessorList.add(beanPostProcessor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setType(clazz);
                    Component component = clazz.getAnnotation(Component.class);
                    String beanName = component.value().trim();
                    if (StringUtils.EMPTY.equals(beanName)) {
                        beanName = toFirstLowerClass(clazz); // 如果没有指定Bean对象的名称,就采用类名首字母小写
                    }
                    if (clazz.isAnnotationPresent(Scope.class)) {
                        Scope scope = clazz.getAnnotation(Scope.class);
                        String scopeValue = scope.value();
                        beanDefinition.setScope(scopeValue);
                    } else {
                        beanDefinition.setScope("singleton");
                    }
                    // 将字节码对象的
                    beanDefinitionMap.put(beanName,beanDefinition);
                }
            }
        }
    }

    private String toFirstLowerClass(Class<?> clazz) {
        char[] array = clazz.getSimpleName().toCharArray();
        array[0] += 32; //将首字母转换为小写
        return String.valueOf(array);
    }


    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object instance = null;
        try {
            // 创建bean对象
            instance = clazz.getConstructor().newInstance();

            // 完成依赖注入, 对加了@Autowired注解的属性进行赋值
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 这里先简写, 直接按照名称进行依赖注入 (实际也可以按照类型Type进行依赖注入)
                    String fieldName = field.getName();
//                    if (!singleObjectMap.containsKey(fieldName)) {
//                        throw new RuntimeException("no such bean found by name:" + fieldName);
//                    }
                    field.setAccessible(true);
//                    field.set(instance, singleObjectMap.get(fieldName));
                    field.set(instance, getBean(fieldName));
                }
            }

            // 对bean对象的属性名称进行注入
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                // bean对象初始化前执行的前置方法
                beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // Bean对象进行初始化
            if (instance instanceof InitializingBean) {
                // 调用Bean对象进行初始化执行的方法
                ((InitializingBean) instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                //  每次创建Bean对象之后, 都会把所有的beanPostProcessor的后置执行一次
                beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean (String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException("No Such bean");
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        String scope = beanDefinition.getScope();
        if ("singleton".equals(scope)) {
            // 单例bean, 直接从单例容器池中拿bean对象
            Object singletonBean =  singleObjectMap.get(beanName); // 由于创建Bean存在先后顺序,依赖注入时可能还没创建想要的bean
            if (Objects.isNull(singletonBean)) {
                // 容器池中还没有该bean对象, 就先创建bean对象, 再放入容器池
                singletonBean = createBean(beanName, beanDefinition);
                singleObjectMap.put(beanName, singletonBean);
            }
            return singletonBean;
        } else {
            // 原型bean, 每次都要创建一个新的Bean对象
            return createBean(beanName, beanDefinition);
        }
    }
}
