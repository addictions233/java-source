package com.one.Parser;

import com.one.config.Configuration;
import org.dom4j.Element;

import java.util.List;

/**
 * @ClassName: XMLMapperBuilder
 * @Description: 解析mybatis的映射配置文件
 * @Author: one
 * @Date: 2022/03/16
 */
public class XMLMapperParser {
    /**
     * mybatis的核心配置对象
     */
    private Configuration configuration;


    /**
     * 构造器
     * @param configuration 核心配置对象
     */
    public XMLMapperParser(Configuration configuration) {
        this.configuration = configuration;
    }

//    public void parse(Element rootElement, Configuration configuration) {
//
//    }

    /**
     * 解析映射配置文件
     * @param rootElement 映射配置文件的根标签
     */
    public void parse(Element rootElement) {
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectElements = (List<Element>)rootElement.elements("select");
        for (Element selectElement : selectElements) {
            // 解析select标签(statement标签)
            XMLStatementParser xmlStatementParser = new XMLStatementParser();
            xmlStatementParser.parseStatementElement(selectElement,namespace, configuration);
        }
    }
}
