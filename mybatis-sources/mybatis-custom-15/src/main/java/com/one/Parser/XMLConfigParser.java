package com.one.Parser;

import com.one.config.Configuration;
import com.one.io.Resources;
import com.one.utils.DocumentUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName: XMLConfigBuilder
 * @Description: 解析mybatis的核心配置文件:mybatis-config.xml
 * @Author: one
 * @Date: 2022/03/16
 */
public class XMLConfigParser {

    private static final Logger LOGGER = Logger.getLogger(XMLConfigParser.class);

    /**
     * 封装了dataSource和mapperStatement
     */
    private Configuration configuration = new Configuration();


    public Configuration parse(Reader reader) {
        return null;
    }

    /**
     * 解析mybatis核心配置文件
     * @param inputStream 配置文件流对象
     * @return Configuration
     */
    public Configuration parse(InputStream inputStream) {
        // 获取dom4j的document对象
        Document document = DocumentUtils.createDocument(inputStream);
        if (document == null) {
            throw new IllegalArgumentException("document is null");
        }
        // 获取核心配置文件中的根标签
        Element rootElement = document.getRootElement();
        Element environments = rootElement.element("environments");
        // 解析environments标签
        parseEnvironments(environments);
        Element mappers = rootElement.element("mappers");
        parseMappers(mappers, configuration);
        return configuration;
    }

    /**
     * 解析mappers标签
     * @param mappers mappers标签
     * @param configuration mybatis的全局配置对象
     */
    private void parseMappers(Element mappers, Configuration configuration) {
        // 获取mapper标签
        List<Element> mapperElements = (List<Element>)mappers.elements("mapper");
        for (Element mapperElement : mapperElements) {
            String resource = mapperElement.attributeValue("resource");
            InputStream inputStream = Resources.getResourceAsStream(resource);
            Document document = DocumentUtils.createDocument(inputStream);
            // configuration对象究竟以成员变量的方式还是局部变量的方式传入到xmlMapperParser对象中?
            XMLMapperParser xmlMapperParser = new XMLMapperParser(configuration);
//            XMLMapperParser xmlMapperParser = new XMLMapperParser();
            // 解析映射配置文件
            xmlMapperParser.parse(document.getRootElement());
        }
    }

    /**
     * 解析核心配置文件中的environments标签, 获取数据库连接配置信息
     * @param environments environments标签
     */
    private void parseEnvironments(Element environments) {
        // 获取xml配置文件中的environments标签的属性值
        String defaultValue = environments.attributeValue("default");
        // 获取environment标签
        List<Element> environment = (List<Element>)environments.elements("environment");
        for (Element element : environment) {
            // 获取environment标签的id属性值
            String idValue = element.attributeValue("id");
            if (StringUtils.equals(defaultValue,idValue)) {
                parseEnvironment(element);
            }
        }
    }

    /**
     * 解析environment标签
     * @param environment environment标签
     */
    private void parseEnvironment(Element environment) {
        List<Element> dataSourceElement = (List<Element>) environment.elements("dataSource");
        // 创建DBCP数据源对象
        BasicDataSource basicDataSource = new BasicDataSource();
        for (Element element : dataSourceElement) {
            // 获取type属性值
            String typeValue = element.attributeValue("type");
            if ("DBCP".equals(typeValue)) {
               Properties properties = parseDataSource(element);
               basicDataSource.setDriverClassName(properties.getProperty("driver"));
               basicDataSource.setUrl(properties.getProperty("url"));
               basicDataSource.setUsername(properties.getProperty("username"));
               basicDataSource.setPassword(properties.getProperty("password"));
            }
        }
        this.configuration.setDataSource(basicDataSource);
    }

    /**
     * 获取数据库连接的四要素,放到properties对象
     * @param dataSourceElement dataSource标签
     * @return Properties
     */
    private Properties parseDataSource(Element dataSourceElement) {
        Properties properties = new Properties();
        List<Element> propertyElement = (List<Element>)dataSourceElement.elements("property");
        for (Element element : propertyElement) {
            properties.put(element.attributeValue("name"),element.attributeValue("value"));
        }
        return properties;
    }

}
