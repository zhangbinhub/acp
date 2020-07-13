package pers.acp.spring.boot.tools

import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import java.io.IOException

/**
 * Http请求相关工具类
 */
object HttpTools {

    private val log = LogFactory.getInstance(HttpTools::class.java)// 日志对象

    /**
     * 通过request获取项目webRoot路径
     *
     * @return 项目webRoot路径
     */
    @JvmStatic
    fun getWebRootPath(request: HttpServletRequest): String {
        val webRoot = request.contextPath
        return if (webRoot == "/") {
            ""
        } else {
            webRoot
        }
    }

    /**
     * 获取客户端发送的内容（xml或json）字符串
     *
     * @param request 请求对象
     * @return 请求内容
     */
    @JvmStatic
    fun getRequestContent(request: HttpServletRequest): String {
        var sis: ServletInputStream? = null
        try {
            sis = request.inputStream
            val size = request.contentLength
            if (size <= 0) {
                return ""
            }
            val buffer = ByteArray(size)
            val dataByte = ByteArray(size)
            var count = 0
            var rbyte: Int
            while (count < size) {
                rbyte = sis!!.read(buffer)
                if (rbyte > 0) {
                    System.arraycopy(buffer, 0, dataByte, count, rbyte)
                    count += rbyte
                } else {
                    break
                }
            }
            return if (count <= 0) {
                ""
            } else String(dataByte, charset(request.characterEncoding))
        } catch (e: IOException) {
            log.error(e.message, e)
            return ""
        } finally {
            if (sis != null) {
                try {
                    sis.close()
                } catch (e: IOException) {
                    log.error(e.message, e)
                }

            }
        }
    }

    /**
     * uri是否被识别
     *
     * @param uri   请求url字符串
     * @param regex 正则表达式
     * @return true|false
     */
    @JvmStatic
    fun isBeIdentifiedUri(uri: String, regex: String): Boolean {
        return CommonTools.regexPattern(regex, uri)
    }

}
