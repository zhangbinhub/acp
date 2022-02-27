package io.github.zhangbinhub.acp.boot.tools

import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.log.LogFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import javax.servlet.http.HttpServletRequest

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
    fun getServerIps() = mutableListOf<String>().apply {
        try {
            val netInterfaces = NetworkInterface.getNetworkInterfaces()
            while (netInterfaces.hasMoreElements()) {
                val ni = netInterfaces.nextElement()
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    this.add(getIpFromInetAddresses(addresses.nextElement()))
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    /**
     * 获取服务器所有网卡的IP“,”分隔
     *
     * @return 所有网卡IP
     */
    @JvmStatic
    fun getServerIpStr(): String = getServerIps().joinToString(separator = ",")

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
            try {
                ipAddress = getIpFromInetAddresses(InetAddress.getLocalHost())
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        if (!CommonTools.isNullStr(ipAddress) && ipAddress!!.length > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.split(",").first()
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
    fun getWebServerIp(request: HttpServletRequest): String = request.localAddr

    /**
     * 获取WEB应用访问服务器的物理地址
     *
     * @param request 请求对象
     * @return 服务器物理地址
     */
    @JvmStatic
    fun getMACAddress(request: HttpServletRequest): String {
        var mac = getMACAddressFromIp(getWebServerIp(request))
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
    @JvmStatic
    fun getMACAddressFromIp(ip: String): String {
        var result = ""
        try {
            val netInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (netInterfaces.hasMoreElements()) {
                val ni = netInterfaces.nextElement()
                var isGet = false
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    if (addressEqual(addresses.nextElement(), ip)) {
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
                    result = sb.toString().uppercase()
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            result = ""
        }

        return result
    }

    private fun getIpFromInetAddresses(address: InetAddress): String = address.hostAddress.let { hostAddress ->
        if (hostAddress.contains("%")) {
            hostAddress.split("%").first()
        } else {
            hostAddress
        }
    }

    private fun addressEqual(address: InetAddress, ipAddress: String): Boolean =
        getIpFromInetAddresses(address) == getIpFromInetAddresses(InetAddress.getByName(ipAddress))

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
        return CommonTools.strFillIn(Integer.toHexString(intValue), 2, 0, "0")
    }
}
