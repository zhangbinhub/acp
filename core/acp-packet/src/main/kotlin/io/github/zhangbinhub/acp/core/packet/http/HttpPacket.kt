package io.github.zhangbinhub.acp.core.packet.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.packet.xml.XmlPacket

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
object HttpPacket {

    /**
     * 日志对象
     */
    private val log = LogFactory.getInstance(HttpPacket::class.java)

    /**
     * url编码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    @JvmStatic
    @JvmOverloads
    fun urlEncoding(url: String, encoding: String = CommonTools.getDefaultCharset()): String =
        try {
            URLEncoder.encode(url, encoding)
        } catch (e: Exception) {
            log.error(e.message, e)
            url
        }

    /**
     * url解码
     *
     * @param url      url字符串
     * @param encoding 字符编码
     * @return 结果字符串
     */
    @JvmStatic
    @JvmOverloads
    fun urlDecoding(url: String, encoding: String = CommonTools.getDefaultCharset()): String =
        try {
            URLDecoder.decode(url, encoding)
        } catch (e: Exception) {
            log.error(e.message, e)
            url
        }

    /**
     * 构建GET请求参数
     *
     * @param url           url字符串
     * @param params        参数
     * @param clientCharset 字符集
     * @return url字符串
     */
    @JvmStatic
    @JvmOverloads
    fun buildGetParam(
        url: String,
        params: Map<String, String>? = null,
        clientCharset: String = CommonTools.getDefaultCharset()
    ): String =
        try {
            var urlStr = url
            params?.let {
                val urlBuilder = StringBuilder(urlStr)
                it.forEach { (key, value) ->
                    run {
                        val sepStr =
                            if (!urlBuilder.toString().contains("?")) {
                                "?"
                            } else {
                                "&"
                            }
                        urlBuilder.append(sepStr).append(key).append("=").append(urlEncoding(value, clientCharset))
                    }
                }
                urlStr = urlBuilder.toString()
            }
            urlStr
        } catch (e: Exception) {
            log.error(e.message, e)
            url
        }

    /**
     * 构建POST请求XML
     *
     * @param params        参数
     * @param rootName      null则使用默认“xml”
     * @param clientCharset 字符集
     * @param isIndent      是否自动格式化
     * @return xml字符串
     */
    @JvmStatic
    @JvmOverloads
    fun buildPostXMLParam(
        params: Map<String, String>,
        rootName: String = "xml",
        clientCharset: String = CommonTools.getDefaultCharset(),
        isIndent: Boolean = false
    ): String {
        val mapper = ObjectMapper()
        val json = mapper.createObjectNode()
        val info = mapper.createObjectNode()
        val children = mapper.createArrayNode()
        params.forEach { entry ->
            val member = mapper.createObjectNode()
            member.put("value", entry.value)
            member.put("isCDATA", true)
            val element = mapper.createObjectNode()
            element.set<ObjectNode>(entry.key, member)
            children.add(element)
        }
        info.set<ArrayNode>("children", children)
        json.set<ObjectNode>(rootName, info)
        return XmlPacket.jsonToXml(json, clientCharset, isIndent)
    }

}
