/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.CompositeConfiguration;
import org.apache.dubbo.common.config.Configuration;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ClassUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.MethodUtils;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.context.ConfigConfigurationAdapter;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.rpc.model.ConsumerMethodModel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.REMOVE_VALUE_PREFIX;
import static org.apache.dubbo.common.utils.ReflectUtils.findMethodByMethodSignature;

/**
 * Utility methods and public methods for parsing configuration
 *
 * @export
 */
public abstract class AbstractConfig implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractConfig.class);
    private static final long serialVersionUID = 4267533505537413570L;

    /**
     * The maximum length of a <b>parameter's value</b>
     */
    private static final int MAX_LENGTH = 200;

    /**
     * The maximum length of a <b>path</b>
     */
    private static final int MAX_PATH_LENGTH = 200;

    /**
     * The rule qualification for <b>name</b>
     */
    private static final Pattern PATTERN_NAME = Pattern.compile("[\\-._0-9a-zA-Z]+");

    /**
     * The rule qualification for <b>multiply name</b>
     */
    private static final Pattern PATTERN_MULTI_NAME = Pattern.compile("[,\\-._0-9a-zA-Z]+");

    /**
     * The rule qualification for <b>method names</b>
     */
    private static final Pattern PATTERN_METHOD_NAME = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");

    /**
     * The rule qualification for <b>path</b>
     */
    private static final Pattern PATTERN_PATH = Pattern.compile("[/\\-$._0-9a-zA-Z]+");

    /**
     * The pattern matches a value who has a symbol
     */
    private static final Pattern PATTERN_NAME_HAS_SYMBOL = Pattern.compile("[:*,\\s/\\-._0-9a-zA-Z]+");

    /**
     * The pattern matches a property key
     */
    private static final Pattern PATTERN_KEY = Pattern.compile("[*,\\-._0-9a-zA-Z]+");

    /**
     * The legacy properties container
     */
    private static final Map<String, String> LEGACY_PROPERTIES = new HashMap<String, String>();

    /**
     * The suffix container
     */
    private static final String[] SUFFIXES = new String[]{"Config", "Bean"};

    static {
        LEGACY_PROPERTIES.put("dubbo.protocol.name", "dubbo.service.protocol");
        LEGACY_PROPERTIES.put("dubbo.protocol.host", "dubbo.service.server.host");
        LEGACY_PROPERTIES.put("dubbo.protocol.port", "dubbo.service.server.port");
        LEGACY_PROPERTIES.put("dubbo.protocol.threads", "dubbo.service.max.thread.pool.size");
        LEGACY_PROPERTIES.put("dubbo.consumer.timeout", "dubbo.service.invoke.timeout");
        LEGACY_PROPERTIES.put("dubbo.consumer.retries", "dubbo.service.max.retry.providers");
        LEGACY_PROPERTIES.put("dubbo.consumer.check", "dubbo.service.allow.no.provider");
        LEGACY_PROPERTIES.put("dubbo.service.url", "dubbo.service.address");

        // this is only for compatibility
        DubboShutdownHook.getDubboShutdownHook().register();
    }

    /**
     * The config id
     */
    protected String id;
    protected String prefix;

    private static String convertLegacyValue(String key, String value) {
        if (value != null && value.length() > 0) {
            if ("dubbo.service.max.retry.providers".equals(key)) {
                return String.valueOf(Integer.parseInt(value) - 1);
            } else if ("dubbo.service.allow.no.provider".equals(key)) {
                return String.valueOf(!Boolean.parseBoolean(value));
            }
        }
        return value;
    }

    private static String getTagName(Class<?> cls) {
        String tag = cls.getSimpleName();
        for (String suffix : SUFFIXES) {
            if (tag.endsWith(suffix)) {
                tag = tag.substring(0, tag.length() - suffix.length());
                break;
            }
        }
        return StringUtils.camelToSplitName(tag, "-");
    }

    protected static void appendParameters(Map<String, String> parameters, Object config) {
        appendParameters(parameters, config, null);
    }

    @SuppressWarnings("unchecked")
    protected static void appendParameters(Map<String, String> parameters, Object config, String prefix) {
        // 把XxConfig中的属性添加到parameters中

        if (config == null) {
            return;
        }
        Method[] methods = config.getClass().getMethods();
        for (Method method : methods) {
            // 遍历某个Config类的get方法和getParameters方法
            try {
                String name = method.getName();
                if (MethodUtils.isGetter(method)) {
                    // 利用get方法来获得key和value，key表示配置项，value表示对应的值
                    // 如果@Parameter注解的key属性有值，则该值为key，否则截取get方法获取到key
                    // 执行get方法获取到value
                    // 对于某些key可以存在多个值，那么这是可以利用@Parameter注解的append属性
                    // 如果prefix存在，则key=prefix+"."+key
                    // 把key和value，put到parameters中

                    Parameter parameter = method.getAnnotation(Parameter.class);

                    // 如果get方法返回类型是Object类型，或者
                    // @Parameter注解存在，但是excluded为true，表示忽略该get方法
                    // 有些参数不需要做为url的参数，比如contextPath
                    if (method.getReturnType() == Object.class || parameter != null && parameter.excluded()) {
                        continue;
                    }

                    // get方法对应的key
                    String key;
                    if (parameter != null && parameter.key().length() > 0) {
                        key = parameter.key();
                    } else {
                        key = calculatePropertyFromGetter(name);
                    }

                    // 执行get方法
                    Object value = method.invoke(config);
                    String str = String.valueOf(value).trim();


                    if (value != null && str.length() > 0) {
                        // 需要编码
                        if (parameter != null && parameter.escaped()) {
                            str = URL.encode(str);
                        }

                        // 是不是把get方法的返回值添加到现有的parameters中去
                        if (parameter != null && parameter.append()) {

                            // 从parameters获取key默认的值
                            String pre = parameters.get(DEFAULT_KEY + "." + key);
                            if (pre != null && pre.length() > 0) {
                                str = pre + "," + str;
                            }

                            // 从parameters获取key对应的值
                            pre = parameters.get(key);
                            if (pre != null && pre.length() > 0) {
                                str = pre + "," + str;
                            }
                        }
                        // 前缀+key
                        // 为什么要有前缀？因为在服务导出时，会存在很多的配置项与值，这些配置项都会存入到map中，防止key冲突
                        // 比如可以针对单个方法进行配置，此时前缀就是方法名
                        if (prefix != null && prefix.length() > 0) {
                            key = prefix + "." + key;
                        }

                        // key对应某个值
                        parameters.put(key, str);
                    } else if (parameter != null && parameter.required()) {
                        throw new IllegalStateException(config.getClass().getSimpleName() + "." + key + " == null");
                    }
                } else if (isParametersGetter(method)) {
                    // getParameters方法

                    // 执行getParameters方法
                    Map<String, String> map = (Map<String, String>) method.invoke(config, new Object[0]);
                    parameters.putAll(convert(map, prefix));
                }
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Deprecated
    protected static void appendAttributes(Map<String, Object> parameters, Object config) {
        appendAttributes(parameters, config, null);
    }

    @Deprecated
    protected static void appendAttributes(Map<String, Object> parameters, Object config, String prefix) {
        if (config == null) {
            return;
        }
        Method[] methods = config.getClass().getMethods();
        for (Method method : methods) {
            try {
                Parameter parameter = method.getAnnotation(Parameter.class);
                if (parameter == null || !parameter.attribute()) {
                    continue;
                }
                String name = method.getName();
                if (MethodUtils.isGetter(method)) {
                    String key;
                    if (parameter.key().length() > 0) {
                        key = parameter.key();
                    } else {
                        key = calculateAttributeFromGetter(name);
                    }
                    Object value = method.invoke(config);
                    if (value != null) {
                        if (prefix != null && prefix.length() > 0) {
                            key = prefix + "." + key;
                        }
                        parameters.put(key, value);
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    protected static ConsumerMethodModel.AsyncMethodInfo convertMethodConfig2AsyncInfo(MethodConfig methodConfig) {
        if (methodConfig == null || (methodConfig.getOninvoke() == null && methodConfig.getOnreturn() == null && methodConfig.getOnthrow() == null)) {
            return null;
        }

        //check config conflict
        if (Boolean.FALSE.equals(methodConfig.isReturn()) && (methodConfig.getOnreturn() != null || methodConfig.getOnthrow() != null)) {
            throw new IllegalStateException("method config error : return attribute must be set true when onreturn or onthrow has been set.");
        }


        ConsumerMethodModel.AsyncMethodInfo asyncMethodInfo = new ConsumerMethodModel.AsyncMethodInfo();

        asyncMethodInfo.setOninvokeInstance(methodConfig.getOninvoke());
        asyncMethodInfo.setOnreturnInstance(methodConfig.getOnreturn());
        asyncMethodInfo.setOnthrowInstance(methodConfig.getOnthrow());

        try {
            String oninvokeMethod = methodConfig.getOninvokeMethod();
            if (StringUtils.isNotEmpty(oninvokeMethod)) {
                asyncMethodInfo.setOninvokeMethod(getMethodByName(methodConfig.getOninvoke().getClass(), oninvokeMethod));
            }

            String onreturnMethod = methodConfig.getOnreturnMethod();
            if (StringUtils.isNotEmpty(onreturnMethod)) {
                asyncMethodInfo.setOnreturnMethod(getMethodByName(methodConfig.getOnreturn().getClass(), onreturnMethod));
            }

            String onthrowMethod = methodConfig.getOnthrowMethod();
            if (StringUtils.isNotEmpty(onthrowMethod)) {
                asyncMethodInfo.setOnthrowMethod(getMethodByName(methodConfig.getOnthrow().getClass(), onthrowMethod));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return asyncMethodInfo;
    }

    private static Method getMethodByName(Class<?> clazz, String methodName) {
        try {
            return ReflectUtils.findMethodByMethodName(clazz, methodName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    protected static void checkExtension(Class<?> type, String property, String value) {
        checkName(property, value);
        if (StringUtils.isNotEmpty(value)
                && !ExtensionLoader.getExtensionLoader(type).hasExtension(value)) {
            throw new IllegalStateException("No such extension " + value + " for " + property + "/" + type.getName());
        }
    }

    /**
     * Check whether there is a <code>Extension</code> who's name (property) is <code>value</code> (special treatment is
     * required)
     *
     * @param type     The Extension type
     * @param property The extension key
     * @param value    The Extension name
     */
    protected static void checkMultiExtension(Class<?> type, String property, String value) {
        checkMultiName(property, value);
        if (StringUtils.isNotEmpty(value)) {
            String[] values = value.split("\\s*[,]+\\s*");
            for (String v : values) {
                if (v.startsWith(REMOVE_VALUE_PREFIX)) {
                    v = v.substring(1);
                }
                if (DEFAULT_KEY.equals(v)) {
                    continue;
                }
                if (!ExtensionLoader.getExtensionLoader(type).hasExtension(v)) {
                    throw new IllegalStateException("No such extension " + v + " for " + property + "/" + type.getName());
                }
            }
        }
    }

    protected static void checkLength(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, null);
    }

    protected static void checkPathLength(String property, String value) {
        checkProperty(property, value, MAX_PATH_LENGTH, null);
    }

    protected static void checkName(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, PATTERN_NAME);
    }

    protected static void checkNameHasSymbol(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, PATTERN_NAME_HAS_SYMBOL);
    }

    protected static void checkKey(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, PATTERN_KEY);
    }

    protected static void checkMultiName(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, PATTERN_MULTI_NAME);
    }

    protected static void checkPathName(String property, String value) {
        checkProperty(property, value, MAX_PATH_LENGTH, PATTERN_PATH);
    }

    protected static void checkMethodName(String property, String value) {
        checkProperty(property, value, MAX_LENGTH, PATTERN_METHOD_NAME);
    }

    protected static void checkParameterName(Map<String, String> parameters) {
        if (CollectionUtils.isEmptyMap(parameters)) {
            return;
        }
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            checkNameHasSymbol(entry.getKey(), entry.getValue());
        }
    }

    protected static void checkProperty(String property, String value, int maxlength, Pattern pattern) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (value.length() > maxlength) {
            throw new IllegalStateException("Invalid " + property + "=\"" + value + "\" is longer than " + maxlength);
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new IllegalStateException("Invalid " + property + "=\"" + value + "\" contains illegal " +
                        "character, only digit, letter, '-', '_' or '.' is legal.");
            }
        }
    }

    protected static Set<String> getSubProperties(Map<String, String> properties, String prefix) {
        return properties.keySet().stream().filter(k -> k.contains(prefix)).map(k -> {
            k = k.substring(prefix.length());
            return k.substring(0, k.indexOf("."));
        }).collect(Collectors.toSet());
    }

    private static String extractPropertyName(Class<?> clazz, Method setter) throws Exception {
        String propertyName = setter.getName().substring("set".length());
        Method getter = null;
        try {
            getter = clazz.getMethod("get" + propertyName);
        } catch (NoSuchMethodException e) {
            getter = clazz.getMethod("is" + propertyName);
        }
        Parameter parameter = getter.getAnnotation(Parameter.class);
        if (parameter != null && StringUtils.isNotEmpty(parameter.key()) && parameter.useKeyAsProperty()) {
            propertyName = parameter.key();
        } else {
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }
        return propertyName;
    }

    private static String calculatePropertyFromGetter(String name) {
        int i = name.startsWith("get") ? 3 : 2;
        return StringUtils.camelToSplitName(name.substring(i, i + 1).toLowerCase() + name.substring(i + 1), ".");
    }

    private static String calculateAttributeFromGetter(String getter) {
        int i = getter.startsWith("get") ? 3 : 2;
        return getter.substring(i, i + 1).toLowerCase() + getter.substring(i + 1);
    }

    private static void invokeSetParameters(Class c, Object o, Map map) {
        try {
            Method method = findMethodByMethodSignature(c, "setParameters", new String[]{Map.class.getName()});
            if (method != null && isParametersSetter(method)) {
                method.invoke(o, map);
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private static Map<String, String> invokeGetParameters(Class c, Object o) {
        try {
            Method method = findMethodByMethodSignature(c, "getParameters", null);
            if (method != null && isParametersGetter(method)) {
                return (Map<String, String>) method.invoke(o);
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    private static boolean isParametersGetter(Method method) {
        String name = method.getName();
        return ("getParameters".equals(name)
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && method.getReturnType() == Map.class);
    }

    private static boolean isParametersSetter(Method method) {
        return ("setParameters".equals(method.getName())
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 1
                && Map.class == method.getParameterTypes()[0]
                && method.getReturnType() == void.class);
    }

    private static Map<String, String> convert(Map<String, String> parameters, String prefix) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        String pre = (prefix != null && prefix.length() > 0 ? prefix + "." : "");

        // 如果有前缀就拼上前缀
        // 为什么要有前缀？因为在服务导出时，会存在很多的配置项与值，这些配置项都会存入到map中，防止key冲突
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result.put(pre + entry.getKey().replace('-', '.'), entry.getValue());
        }
        return result;
    }

    @Parameter(excluded = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void updateIdIfAbsent(String value) {
        if (StringUtils.isNotEmpty(value) && StringUtils.isEmpty(id)) {
            this.id = value;
        }
    }

    protected void appendAnnotation(Class<?> annotationClass, Object annotation) {
        Method[] methods = annotationClass.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class
                    && method.getReturnType() != void.class
                    && method.getParameterTypes().length == 0
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {
                try {
                    String property = method.getName();
                    if ("interfaceClass".equals(property) || "interfaceName".equals(property)) {
                        property = "interface";
                    }
                    String setter = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
                    Object value = method.invoke(annotation);
                    if (value != null && !value.equals(method.getDefaultValue())) {
                        Class<?> parameterType = ReflectUtils.getBoxedClass(method.getReturnType());
                        if ("filter".equals(property) || "listener".equals(property)) {
                            parameterType = String.class;
                            value = StringUtils.join((String[]) value, ",");
                        } else if ("parameters".equals(property)) {
                            parameterType = Map.class;
                            value = CollectionUtils.toStringMap((String[]) value);
                        }
                        try {
                            Method setterMethod = getClass().getMethod(setter, parameterType);
                            setterMethod.invoke(this, value);
                        } catch (NoSuchMethodException e) {
                            // ignore
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Should be called after Config was fully initialized.
     * // FIXME: this method should be completely replaced by appendParameters
     *
     * @return
     * @see AbstractConfig#appendParameters(Map, Object, String)
     * <p>
     * Notice! This method should include all properties in the returning map, treat @Parameter differently compared to appendParameters.
     */
    public Map<String, String> getMetaData() {
        Map<String, String> metaData = new HashMap<>();
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            try {
                String name = method.getName();
                if (MethodUtils.isMetaMethod(method)) {
                    String key;
                    Parameter parameter = method.getAnnotation(Parameter.class);
                    if (parameter != null && parameter.key().length() > 0 && parameter.useKeyAsProperty()) {
                        key = parameter.key();
                    } else {
                        key = calculateAttributeFromGetter(name);
                    }
                    // treat url and configuration differently, the value should always present in configuration though it may not need to present in url.
                    //if (method.getReturnType() == Object.class || parameter != null && parameter.excluded()) {
                    if (method.getReturnType() == Object.class) {
                        metaData.put(key, null);
                        continue;
                    }

                    /**
                     * Attributes annotated as deprecated should not override newly added replacement.
                     */
                    if (MethodUtils.isDeprecated(method) && metaData.get(key) != null) {
                        continue;
                    }

                    Object value = method.invoke(this);
                    String str = String.valueOf(value).trim();
                    if (value != null && str.length() > 0) {
                        metaData.put(key, str);
                    } else {
                        metaData.put(key, null);
                    }
                } else if (isParametersGetter(method)) {
                    Map<String, String> map = (Map<String, String>) method.invoke(this, new Object[0]);
                    metaData.putAll(convert(map, ""));
                }
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return metaData;
    }

    @Parameter(excluded = true)
    public String getPrefix() {
        return StringUtils.isNotEmpty(prefix) ? prefix : (CommonConstants.DUBBO + "." + getTagName(this.getClass()));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // 刷新XxConfig
    // 一个XxConfig对象的属性可能是有值的，也可能是没有值的，这时需要从其他位置获取属性值,来进行属性的覆盖
    // 覆盖的优先级，从大到小为系统变量->配置中心应用配置->配置中心全局配置->注解或xml中定义->dubbo.properties文件

    // 以ServiceConfig为例，ServiceConfig中包括很多属性，比如timeout
    // 但是在定义一个Service时，如果在注解上没有配置timeout，那么就会其他地方获取timeout的配置
    // 比如可以从系统变量->配置中心应用配置->配置中心全局配置->注解或xml中定义->dubbo.properties文件
    // refresh是刷新，将当前ServiceConfig上的set方法所对应的属性更新为优先级最高的值
    public void refresh() {
        try {
            CompositeConfiguration compositeConfiguration = Environment.getInstance().getConfiguration(getPrefix(), getId());

            // 表示XxConfig对象本身- AbstractConfig
            Configuration config = new ConfigConfigurationAdapter(this);
            // 判断配置是否是配置中心优先, 决定配置的优先级
            if (Environment.getInstance().isConfigCenterFirst()) {
                // The sequence would be: SystemConfiguration -> AppExternalConfiguration -> ExternalConfiguration -> AbstractConfig -> PropertiesConfiguration
                compositeConfiguration.addConfiguration(4, config);
            } else {
                // The sequence would be: SystemConfiguration -> AbstractConfig -> AppExternalConfiguration -> ExternalConfiguration -> PropertiesConfiguration
                compositeConfiguration.addConfiguration(2, config);
            }

            // loop methods, get override value and set the new value back to method
            //
            Method[] methods = getClass().getMethods();
            for (Method method : methods) {
                // 是不是setXX()方法
                if (MethodUtils.isSetter(method)) {
                    // 获取xx配置项的value
                    String value = StringUtils.trim(compositeConfiguration.getString(extractPropertyName(getClass(), method)));
                    // isTypeMatch() is called to avoid duplicate and incorrect update, for example, we have two 'setGeneric' methods in ReferenceConfig.
                    if (StringUtils.isNotEmpty(value) && ClassUtils.isTypeMatch(method.getParameterTypes()[0], value)) {
                        method.invoke(this, ClassUtils.convertPrimitive(method.getParameterTypes()[0], value));
                    }
                  // 是不是setParameters()方法
                } else if (isParametersSetter(method)) {
                    // 获取parameter配置项的value
                    String value = StringUtils.trim(compositeConfiguration.getString(extractPropertyName(getClass(), method)));
                    if (StringUtils.isNotEmpty(value)) {
                        Map<String, String> map = invokeGetParameters(getClass(), this);
                        map = map == null ? new HashMap<>() : map;
                        map.putAll(convert(StringUtils.parseParameters(value), ""));
                        invokeSetParameters(getClass(), this, map);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to override ", e);
        }
    }

    @Override
    public String toString() {
        try {
            StringBuilder buf = new StringBuilder();
            buf.append("<dubbo:");
            buf.append(getTagName(getClass()));
            Method[] methods = getClass().getMethods();
            for (Method method : methods) {
                try {
                    if (MethodUtils.isGetter(method)) {
                        String name = method.getName();
                        String key = calculateAttributeFromGetter(name);
                        Object value = method.invoke(this);
                        if (value != null) {
                            buf.append(" ");
                            buf.append(key);
                            buf.append("=\"");
                            buf.append(value);
                            buf.append("\"");
                        }
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            buf.append(" />");
            return buf.toString();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
            return super.toString();
        }
    }

    /**
     * FIXME check @Parameter(required=true) and any conditions that need to match.
     */
    @Parameter(excluded = true)
    public boolean isValid() {
        return true;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj.getClass().getName().equals(this.getClass().getName()))) {
            return false;
        }

        Method[] methods = this.getClass().getMethods();
        for (Method method1 : methods) {
            if (MethodUtils.isGetter(method1)) {
                Parameter parameter = method1.getAnnotation(Parameter.class);
                if (parameter != null && parameter.excluded()) {
                    continue;
                }
                try {
                    Method method2 = obj.getClass().getMethod(method1.getName(), method1.getParameterTypes());
                    Object value1 = method1.invoke(this, new Object[]{});
                    Object value2 = method2.invoke(obj, new Object[]{});
                    if ((value1 != null && value2 != null) && !value1.equals(value2)) {
                        return false;
                    }
                } catch (Exception e) {
                    return true;
                }
            }
        }
        return true;
    }
}
