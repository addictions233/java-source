package com.one.utils;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.Reader;

/**
 * @ClassName: DocumentUtils
 * @Description: 获取Dom4j的document对象的工具类
 * @Author: one
 * @Date: 2022/03/17
 */
public class DocumentUtils {
    private static final Logger LOGGER = Logger.getLogger(DocumentUtils.class);

    /**
     * 获取dom4j的document对象的工具方法
     * @param inputStream 资源输入流对象
     * @return Document
     */
    public static Document createDocument(InputStream inputStream) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            LOGGER.error("parse mybatis-config.xml error:", e);
        }
        return null;
    }

    public static Document createDocument(Reader reader) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            return document;
        } catch (DocumentException e) {
            LOGGER.error("parse mybatis-config.xml error:", e);
        }
        return null;
    }
}
