package com.one.Parser;

import com.one.config.Configuration;
import com.one.config.MapperStatement;
import com.one.framework.sqlsource.SqlSource;
import com.one.utils.SimpleTypeRegistry;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

/**
 * @ClassName: XMLStatementBuilder
 * @Description: 解析CRUD标签,或者称为statement标签
 * @Author: one
 * @Date: 2022/03/16
 */
public class XMLStatementParser {
    public void parseStatementElement(Element selectElement, String namespace, Configuration configuration) {
        String idValue = selectElement.attributeValue("id");
        // 对id值进行非空判断
        if (StringUtils.isEmpty(idValue)) {
            return;
        }
        // statement唯一标识
        String statementId= namespace + "." + idValue;
        String parameterType = selectElement.attributeValue("parameterType");
        // 参数类型
        Class<?> parameterTypeClass = SimpleTypeRegistry.getClassByName(parameterType);
        String resultType = selectElement.attributeValue("resultType");
        // 结果类型
        Class<?> resultTypeClass = SimpleTypeRegistry.getClassByName(resultType);
        // statement类型
        String statementType = selectElement.attributeValue("statementType");
        statementType = StringUtils.isEmpty(statementId) ? "prepared" : statementType;
        // 解析select标签的内容,获取sqlSource对象
        XMLScriptParser xmlScriptParser = new XMLScriptParser();
        SqlSource sqlSource = xmlScriptParser.createSqlSource(selectElement);
        // 构造mapperStatement对象
        MapperStatement mapperStatement = new MapperStatement(statementId,sqlSource, statementType,parameterTypeClass,resultTypeClass);
        // 添加到全局配置对象中
        configuration.putMapperStatement(statementId,mapperStatement);
    }
}
