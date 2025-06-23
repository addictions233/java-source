package com.one.Parser;

import com.one.framework.sqlnode.SqlNode;
import com.one.framework.sqlnode.support.IfSqlNode;
import com.one.framework.sqlnode.support.MixedSqlNode;
import com.one.framework.sqlnode.support.StaticTextSqlNode;
import com.one.framework.sqlnode.support.TextSqlNode;
import com.one.framework.sqlsource.SqlSource;
import com.one.framework.sqlsource.support.DynamicSqlSource;
import com.one.framework.sqlsource.support.RawSqlSource;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: XMLScriptBuilder
 * @Description: 解析sqlNode脚本
 * @Author: one
 * @Date: 2022/03/16
 */
public class XMLScriptParser {
    private Boolean isDynamic = false;

    public SqlSource createSqlSource(Element selectElement) {
        return parseScriptSqlNode(selectElement);
    }

    private SqlSource parseScriptSqlNode(Element selectElement) {
        // 将select标签内容封装为多个sqlNode集合,然后封装到MixedSqlNode对象中
        MixedSqlNode mixedSqlNode = parseDynamicTags(selectElement);
        SqlSource sqlSource;
        if (isDynamic) { // 包含 ${}
            sqlSource = new DynamicSqlSource(mixedSqlNode);
        } else { // 不包含 ${}, 只包含#{}
            sqlSource = new RawSqlSource(mixedSqlNode);
        }
        return sqlSource;
    }

    private MixedSqlNode parseDynamicTags(Element selectElement) {
        // 将sql脚本解析并封装成对应的sqlNode对象,然后放入sqlNodeList集合中
        List<SqlNode> sqlNodeList = new ArrayList<>();
        int count = selectElement.nodeCount();
        for (int i = 0; i < count; i++) {
            Node node = selectElement.node(i);
            if (node instanceof Text) { // 文本类型的node
                String sqlText = node.getText();
                if (StringUtils.isBlank(sqlText)) {
                    continue;
                }
                // 叫sql脚本封装成textSqlNode对象
                TextSqlNode textSqlNode = new TextSqlNode(sqlText);
                if (textSqlNode.isDynamic()) {
                    this.isDynamic = true;
                    sqlNodeList.add(textSqlNode); // sql脚本中包含${},用textSqlNode封装
                } else { // sql脚本中不包含${},用staticTextSqlNode封装
                    sqlNodeList.add(new StaticTextSqlNode(sqlText));
                }
            } else if (node instanceof Element) {
                // 因为无论动态标签还是 ${}都需要拿到入参param,然后替换内容的,所以isDynamic为true
                this.isDynamic = true;
                Element element = (Element) node;
                // 获取标签名称
                String name = element.getName();
                if ("if".equals(name)) {
                    String test = element.attributeValue("test");
                    // 递归解析if标签的子节点
                    MixedSqlNode mixedSqlNode = parseDynamicTags(element);
                    IfSqlNode ifSqlNode = new IfSqlNode(test, mixedSqlNode);
                    sqlNodeList.add(ifSqlNode);
                } else if ("where".equals(name)) { // 解析where标签
                    // TODO 使用策略模式对多种类型的动态标签进行解析
                }


            }
        }
        return new MixedSqlNode(sqlNodeList);
    }

}
