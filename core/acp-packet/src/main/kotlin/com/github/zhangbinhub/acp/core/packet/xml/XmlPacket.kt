package com.github.zhangbinhub.acp.core.packet.xml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.naming.NoNameCoder
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.security.AnyTypePermission
import org.dom4j.*
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import com.github.zhangbinhub.acp.core.CommonTools
import com.github.zhangbinhub.acp.core.log.LogFactory

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringWriter

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
object XmlPacket {

    private val log = LogFactory.getInstance(XmlPacket::class.java)

    /**
     * 解析XML获取Map对象
     *
     * @param xml 根节点下只有一级子节点
     * @return 参数Map
     */
    @JvmStatic
    fun parseXml(xml: String): MutableMap<String, String> =
        try {
            val result: MutableMap<String, String> = mutableMapOf()
            val json = xmlToJson(xml)
            val iKey = json.fields()
            while (iKey.hasNext()) {
                val node = iKey.next()
                val info = node.value
                val tags = info.get("children")
                for (element in tags) {
                    val items = element.fields()
                    while (items.hasNext()) {
                        val item = items.next()
                        val itemInfo = item.value
                        result[item.key] = itemInfo.get("value").textValue()
                    }
                }
            }
            result
        } catch (e: Exception) {
            log.error(e.message, e)
            mutableMapOf()
        }

    /**
     * json对象转换为xml字符串
     *
     * @param json          {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    @JvmStatic
    @JvmOverloads
    fun jsonToXml(
        json: JsonNode,
        clientCharset: String = CommonTools.getDefaultCharset(),
        isIndent: Boolean = false
    ): String {
        var writer: StringWriter? = null
        var output: XMLWriter? = null
        try {
            if (json.isNull) {
                throw Exception("json object is null")
            }
            val document = DocumentHelper.createDocument()
            val iKey = json.fields()
            while (iKey.hasNext()) {
                val key = iKey.next()
                val info = key.value
                val root = document.addElement(key.key)
                generateXMLElementByJSON(root, info)
            }
            val format = OutputFormat.createCompactFormat()
            format.encoding = clientCharset
            format.isNewlines = isIndent
            format.setIndent(isIndent)
            format.indent = "    "
            writer = StringWriter()
            output = XMLWriter(writer, format)
            output.write(document)
            writer.close()
            output.close()
            return writer.toString()
        } catch (e: Exception) {
            try {
                writer?.close()
            } catch (ex: IOException) {
                log.error(ex.message, ex)
            }

            try {
                output?.close()
            } catch (ex: IOException) {
                log.error(ex.message, ex)
            }

            log.error(e.message, e)
            return ""
        }

    }

    /**
     * xml字符串转换为json对象
     *
     * @param xml xml字符串
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    @JvmStatic
    fun xmlToJson(xml: String): JsonNode =
        try {
            val sax = SAXReader()
            val document = sax.read(ByteArrayInputStream(xml.toByteArray()))
            val root = document.rootElement
            generateJSONByXML(root)
        } catch (e: DocumentException) {
            log.error(e.message, e)
            val mapper = ObjectMapper()
            mapper.createObjectNode()
        }

    /**
     * xml字符串转对象
     *
     * @param xmlStr xml字符串
     * @param cls    类型
     * @return 对象
     */
    @JvmStatic
    inline fun <reified T> xmlToObject(xmlStr: String, cls: Class<T>): T? {
        val xStream = XStream(DomDriver())
        xStream.addPermission(AnyTypePermission.ANY)
        xStream.processAnnotations(cls)
        xStream.ignoreUnknownElements()
        xStream.autodetectAnnotations(true)
        return xStream.fromXML(xmlStr) as? T
    }

    /**
     * 对象转xml字符串
     *
     * @param obj     对象
     * @param encoding 字符集
     * @return xml字符串
     */
    @JvmStatic
    @JvmOverloads
    fun objectToXML(obj: Any, encoding: String = CommonTools.getDefaultCharset()): String {
        val xStream = XStream(DomDriver(encoding, NoNameCoder()))
        xStream.autodetectAnnotations(true)
        return xStream.toXML(obj)
    }

    /**
     * 通过json生成xml节点
     *
     * @param parent 父节点
     * @param obj    {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    @Throws(Exception::class)
    private fun generateXMLElementByJSON(parent: Element, obj: JsonNode) {
        var value = ""
        var isdoc = true
        val children: JsonNode
        if (obj.has("children")) {
            if (obj.get("children").isArray) {
                children = obj.get("children")
                for (json in children) {
                    val iKey = json.fields()
                    while (iKey.hasNext()) {
                        val key = iKey.next()
                        val element = parent.addElement(key.key)
                        val info = key.value
                        generateXMLElementByJSON(element, info)
                    }
                }
            } else {
                throw Exception("children need jsonArray")
            }
        } else {
            if (obj.has("value")) {
                value = obj.get("value").textValue()
            }
            if (obj.has("isCDATA")) {
                isdoc = obj.get("isCDATA").asBoolean()
            }
            if (isdoc) {
                parent.addCDATA(value)
            } else {
                parent.addText(value)
            }
        }
    }

    /**
     * 生成json对象
     *
     * @param element xml元素对象
     * @return {tagname:{value:string,isCDATA:boolean,children:[{},{}]}}
     */
    private fun generateJSONByXML(element: Element): JsonNode {
        val mapper = ObjectMapper()
        val result = mapper.createObjectNode()
        val info = mapper.createObjectNode()
        if (!element.isTextOnly) {
            val jsonChildren = mapper.createArrayNode()
            val children = element.elements()
            for (aChildren in children) {
                val child = aChildren as Element
                jsonChildren.add(generateJSONByXML(child))
            }
            info.set("children", jsonChildren)
        } else {
            info.put("value", element.textTrim)
            if (isCDATA(element)) {
                info.put("isCDATA", true)
            } else {
                info.put("isCDATA", false)
            }
        }
        result.set<ObjectNode>(element.name, info)
        return result
    }

    /**
     * 判断节点文本是否是CDATA类型
     *
     * @param node xml节点对象
     * @return 是否是CDATA类型
     */
    private fun isCDATA(node: Node): Boolean {
        if (!node.hasContent())
            return false
        for (o in (node as Branch).content()) {
            val n = o as Node
            if (Node.CDATA_SECTION_NODE == n.nodeType) {
                return true
            }
        }
        return false
    }

}
