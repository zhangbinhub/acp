package pers.acp.spring.cloud.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.commons.util.InetUtils
import pers.acp.core.CommonTools
import pers.acp.spring.boot.component.ServerTools
import java.net.UnknownHostException

class CloudTools(private val serverTools: ServerTools, private val inetUtils: InetUtils) {
    @Value("\${server.address:}")
    private var ip: String? = null
    fun getServerPort(): Int = serverTools.getServerPort()
    fun getServerIp(): String {
        return if (CommonTools.isNullStr(ip)) {
            try {
                inetUtils.findFirstNonLoopbackHostInfo().ipAddress ?: serverTools.getServerIp()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                ""
            }
        } else {
            ip!!
        }
    }
}