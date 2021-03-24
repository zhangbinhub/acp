package pers.acp.spring.cloud.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.commons.util.InetUtils
import pers.acp.core.CommonTools
import java.net.InetAddress
import java.net.UnknownHostException

class CloudTools(private val inetUtils: InetUtils) {
    @Value("\${server.address:}")
    private var serverIp: String? = null

    fun getServerIp(): String? {
        return if (CommonTools.isNullStr(serverIp)) {
            try {
                inetUtils.findFirstNonLoopbackHostInfo().ipAddress ?: InetAddress.getLocalHost().hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                ""
            }
        } else {
            serverIp
        }
    }
}