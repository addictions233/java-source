package com.one.mvcframework.v1.servlet;

import com.one.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: DispatcherServlet
 * @Description: 手写DispatcherServlet的v1版本
 * @Author: one
 * @Date: 2022/04/20
 */
public class DispatcherServlet extends HttpServlet {
    /**
     * 封装application.properties配置文件中的内容
     */
    private final Properties contextConfig = new Properties();

    /**
     * 保存扫描包下扫描到的所有的类名
     */
    List<String> classNameList = new ArrayList<>();

    /**
     * 用来保存创建的bean对象的IOC容器, key是bean对象的名称, value是bean对象
     */
    private final Map<String, Object> IOC = new ConcurrentHashMap<>();

    /**
     * 将URL和Method进行一对一的关联映射Map<String, Method>
     */
    private final Map<String, Method> handlerMapping = new ConcurrentHashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 6,调用dispatcherServlet对象,完成请求响应处理,dispatcherServlet会拦截所有请求
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Server Error!");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        // 获取请求资源定位符
        String requestURI = req.getRequestURI();
        // 获取项目的上下文路径
        String contextPath = req.getContextPath();
        // 将请求资源定位符中的上下文路径替换掉, 获取真实的请求资源路径
        String url = requestURI.replace(contextPath, "").replaceAll("/+", "/");
        // 如果映射器处理器中没有对应的请求url,返回404
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Fund");
            return;
        }
        // 获取对应的处理方法
        Method handlerMethod = handlerMapping.get(url);
        // 找到该方法对应的controller对象
        String controllerBeanName = toFirstLowerCase(handlerMethod.getDeclaringClass());

        // 获取req请求对象中封装的参数,可以查看源码返回的是Map<String, String[]>类型,因为同一参数name,可以赋值多次,所以用String[]数组接收
        Map<String, String[]> parameterMap = req.getParameterMap();
        // 处理参数,对handlerMethod上的参数进行赋值
//        Class<?>[] parameterTypes = handlerMethod.getParameterTypes(); 可以通过类型判断对参数进行顺序赋值
        Parameter[] parameters = handlerMethod.getParameters();
        // 顺序封装handlerMethod方法中需要的参数,类似于mybatis的参数处理
        Object[] paramArray = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if(parameter.getType() == HttpServletRequest.class) {
                paramArray[i] = req;
            } else if (parameter.getType() == HttpServletResponse.class) {
                paramArray[i] = resp;
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (parameter.getType().isArray()) {
                    paramArray[i] = parameterMap.get(requestParam.value());  // 这里直接取第一个值有问题,理论上可能是一个数组
                } else {
                    paramArray[i] = Arrays.toString(parameterMap.get(requestParam.value())) // 通过@RequstParam注解注入字符串参数
                    .replaceAll("\\[|\\]",""); // 将数组中的[]去掉
                }
            } else {
                paramArray[i] = null;
            }
        }
        // 处理方法调用
        Object result = handlerMethod.invoke(IOC.get(controllerBeanName), paramArray);
        resp.getWriter().write(result.toString());
    }

    /**
     * servlet规范: 所有的servlet启动时都会调用init()方法
     *
     * @param config 在web.xml中配置的 servlet的init param
     */
    @Override
    public void init(ServletConfig config) {
        // 1,加载application.properties配置文件
        doLoadConfig(config);

        // 2,扫描相关包下的类
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3,初始化扫描到的类并加入到IOC容器中
        doInstance();

        // 4, 完成DI依赖注入
        doAutowired();

        // 5, 初始化处理器映射器HandlerMapping
        doHandlerMapping();
    }


    /**
     * 通过上下文配置文件路径读取配置文件,将配置信息保存到全局变量contextConfig对象中
     *
     * @param config servlet的初始化配置对象
     */
    private void doLoadConfig(ServletConfig config) {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描配置路径下所有的类,获取类名
     *
     * @param scanPackage 扫描包路径
     */
    private void doScanner(final String scanPackage) {
        // 将包名替换为对应的文件路径,就是把其中的.号换成/号
        String path = scanPackage.replaceAll("\\.", "/");
        // 获取资源文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + path);
        // 通过url对象构建file对象,该路径就是要扫描的包
        if (url == null) {
            throw new IllegalArgumentException("scanPackage is not valid: " + scanPackage);
        }
        File classPath = new File(url.getFile());
        if (classPath.listFiles() == null) {
            return;
        }
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                // 如果是文件夹,就继续递归扫描包下的类
                doScanner(scanPackage + "." + file.getName());
            } else {
                // 如果是文件,就保存字节码对象的全限定类名
                if (!file.getName().endsWith(".java")) {
                    continue; // 非字节码对象
                }
                // 保存扫描到的字节码的全限定类名
                String className = scanPackage + "." + file.getName().replace(".java", "");
                classNameList.add(className);
            }
        }
    }

    /**
     * 对于使用@Service和@Controller的类创建bean对象,并加入到IOC容器中
     */
    private void doInstance() {
        if (classNameList.isEmpty()) {
            // 如果需要初始化的类为空,直接返回
            return;
        }
        for (String className : classNameList) {
            try {
                Class<?> clazz = Class.forName(className);
                // 使用@Servivce和@Controller注解的类才创建bean对象
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Controller controller = clazz.getAnnotation(Controller.class);
                    Object instance = clazz.newInstance();
                    // bean对象的名称为controller注解中的value属性值
                    String beanName = controller.value().trim();
                    if ("".equals(controller.value().trim())) {
                        // 如果value属性没给值,那么bean对象的名称是类名首字母改小写
                        beanName = toFirstLowerCase(clazz);
                    }
                    // 将对象放入IOC容器中
                    IOC.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    Object instance = clazz.newInstance();
                    String beanName = service.value().trim();
                    if ("".equals(beanName)) {
                        // bean对象的名称采用类名首字母小写
                        beanName = toFirstLowerCase(clazz);
                    }
                    // 将对象放入IOC容器中,bean对象的名称是类名首字母改小写
                    IOC.put(beanName, instance);
                    // service层实现了接口,所以创建bean对象时还得把其实现的接口加入ioc容器
                    Class<?>[] clazzInterfaces = clazz.getInterfaces();
                    for (Class<?> clazzInterface : clazzInterfaces) {
//                        Object interfaceInstance = clazzInterface.newInstance(); // 接口是无法实例化的
                        // 这里对接口的bean对象名称做简化处理,直接按照类型进行注入
                        String interfaceBeanName = clazzInterface.getName();
                        if (IOC.containsKey(interfaceBeanName)) {
                            throw new RuntimeException("包含重复的bean对象名称:" + interfaceBeanName);
                        }
                        IOC.put(interfaceBeanName, instance);
                    }
                }
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String toFirstLowerCase(Class<?> clazz) {
        char[] array = clazz.getSimpleName().toCharArray();
        array[0] += 32;
        return String.valueOf(array);
    }

    /**
     * 对于bean对象中使用@Autowired注解到的属性完成DI属性注入
     */
    private void doAutowired() {
        Collection<Object> beanCollections = IOC.values();
        // 对bean对象进行依赖注入
        for (Object bean : beanCollections) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowried.class)) {
                    continue;
                }
                Autowried autowried = field.getAnnotation(Autowried.class);
                // 获取autowired注解中的value属性值
                String beanName = autowried.value().trim();
                if ("".equals(beanName)) {
                    // 获取字段类型,按照类型进行注入, 类型注入不能这么写,这里简写
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    // 完成注入
                    field.set(bean, IOC.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取@Controller注解的路径,初始化处理器映射器
     */
    private void doHandlerMapping() {
        Collection<Object> beanCollections = IOC.values();
        for (Object bean : beanCollections) {
            Class<?> clazz = bean.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                // controller类上的requestMapping注解的路径
                baseUrl = clazz.getAnnotation(RequestMapping.class).value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                String methodUrl = baseUrl + "/" + methodRequestMapping.value();
                // 防止路径中出现两个//
                methodUrl = methodUrl.replaceAll("/+", "/");
                // 将路径和方法保存到处理器映射器中
                handlerMapping.put(methodUrl, method);
            }
        }
    }
}
