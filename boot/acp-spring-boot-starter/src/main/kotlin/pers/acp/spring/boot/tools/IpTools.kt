package pers.acp.spring.boot.tools

import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory

import javax.servlet.http.HttpServletRequest
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and

/**
 * IP 相关工具类
 */
object IpTools {

    private val log = LogFactory.getInstance(IpTools::class.java)// 日志对象

    /**
     * 获取服务器所有网卡的IP
     *
     * @return 网卡对应IP
     */
    @JvmStatic
    fun getServerIps(): List<String> {
        val ips = arrayListOf<String>()
        try {
            val tmpIps: ArrayList<String> = arrayListOf()
            val netInterfaces = NetworkInterface.getNetworkInterfaces()
            while (netInterfaces.hasMoreElements()) {
                val ni = netInterfaces.nextElement()
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addressIp = StringBuilder()
                    val address = addresses.nextElement().address
                    for (i in address.indices) {
                        if (i > 0) {
                            addressIp.append(".")
                        }
                        addressIp.append(address[i] and 0xFF.toByte())
                    }
                    tmpIps.add(addressIp.toString())
                }
            }
            tmpIps.forEach { item ->
                run {
                    ips.add(item)
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        return ips
    }

    /**
     * 获取服务器所有网卡的IP“,”分隔
     *
     * @return 所有网卡IP
     */
    @JvmStatic
    fun getServerIpStr(): String {
        var ips = ""
        getServerIps().forEach { item ->
            run {
                if (!CommonTools.isNullStr(ips)) {
                    ips += ","
                }
                ips += item
            }
        }
        return ips
    }

    /**
     * 获取远程客户端IP
     *
     * @param request 请求对象
     * @return IP
     */
    @JvmStatic
    fun getRemoteIP(request: HttpServletRequest): String? {
        var ipAddress: String? = request.getHeader("X-Forwarded-For")
        if (CommonTools.isNullStr(ipAddress) || "unknown".equals(ipAddress!!, ignoreCase = true)) {
            ipAddress = request.getHeader("Citrix-Client-IP")
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equals(ipAddress!!, ignoreCase = true)) {
            ipAddress = request.getHeader("Proxy-Client-IP")
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equals(ipAddress!!, ignoreCase = true)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP")
        }
        if (CommonTools.isNullStr(ipAddress) || "unknown".equals(ipAddress!!, ignoreCase = true)) {
            ipAddress = getRemoteRealIP(request)
        }
        if (!CommonTools.isNullStr(ipAddress) && ipAddress!!.length > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","))
            }
        }
        return ipAddress
    }

    /**
     * 获取远程客户端真实底层IP
     *
     * @param request 请求对象
     * @return IP
     */
    private fun getRemoteRealIP(request: HttpServletRequest): String? {
        var ipAddress: String? = request.remoteAddr
        if (ipAddress == "127.0.0.1" || ipAddress == "0:0:0:0:0:0:0:1" || ipAddress == "::1") {
            // 根据网卡取本机配置的IP
            var inet: InetAddress? = null
            try {
                inet = InetAddress.getLocalHost()
            } catch (e: Exception) {
                log.error(e.message, e)
            }

            ipAddress = inet?.hostAddress
        }
        if (!CommonTools.isNullStr(ipAddress) && ipAddress!!.length > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","))
            }
        }
        return ipAddress
    }

    /**
     * 获取WEB应用访问服务器的IP
     *
     * @param request 请求对象
     * @return 服务器IP
     */
    @JvmStatic
    fun getWebServerIp(request: HttpServletRequest): String {
        return request.localAddr
    }

    /**
     * 获取WEB应用访问服务器的物理地址
     *
     * @param request 请求对象
     * @return 服务器物理地址
     */
    @JvmStatic
    fun getMACAddress(request: HttpServletRequest): String {
        val ip = getWebServerIp(request)
        var mac = getMACFromIp(ip)
        if (CommonTools.isNullStr(mac)) {
            mac = CommonTools.getUuid()
        }
        return mac
    }

    /**
     * 通过IP地址获取对应网络接口的物理地址
     *
     * @param ip 服务器IP
     * @return 服务器物理地址
     */
    private fun getMACFromIp(ip: String): String {
        var result = ""
        try {
            val netInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (netInterfaces.hasMoreElements()) {
                val ni = netInterfaces.nextElement()
                var isGet = false
                val address = ni.inetAddresses
                while (address.hasMoreElements()) {
                    val addressIp = StringBuilder()
                    val bytes = address.nextElement().address
                    for (i in bytes.indices) {
                        if (i > 0) {
                            addressIp.append(".")
                        }
                        addressIp.append(bytes[i] and 0xFF.toByte())
                    }
                    if (addressIp.toString() == ip) {
                        isGet = true
                        break
                    }
                }
                if (isGet) {
                    val sb = StringBuilder()
                    ni.hardwareAddress?.let {
                        if (it.size > 1) {
                            sb.append(parseByte(it[0])).append("-")
                                    .append(parseByte(it[1])).append("-")
                                    .append(parseByte(it[2])).append("-")
                                    .append(parseByte(it[3])).append("-")
                                    .append(parseByte(it[4])).append("-")
                                    .append(parseByte(it[5]))
                        }
                    }
                    result = sb.toString().toUpperCase()
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            result = ""
        }

        return result
    }

    /**
     * 格式化二进制
     *
     * @param b 字节
     * @return 字符串
     */
    private fun parseByte(b: Byte): String {
        val intValue: Int = if (b >= 0) {
            b.toInt()
        } else {
            256 + b
        }
        return Integer.toHexString(intValue)
    }
}
