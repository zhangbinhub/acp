package com.github.zhangbinhub.acp.boot.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import com.github.zhangbinhub.acp.core.CommonTools
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger

class ServerTools : ApplicationListener<WebServerInitializedEvent> {
    private val logAdapter = BootLogAdapter()

    @Value("\${server.address:}")
    private var ip: String? = null
    private val port = AtomicInteger(0)
    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        val context: ApplicationContext = event.applicationContext
        if (context is ConfigurableWebServerApplicationContext) {
            if ("management" == context.serverNamespace) {
                return
            }
        }
        this.port.compareAndSet(0, event.webServer.port)
        logAdapter.info("Web Server has started, listening to port: ${port.get()}")
    }

    fun getServerPort(): Int = port.get()

    fun getServerIp(): String = if (CommonTools.isNullStr(ip)) {
        try {
            InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ""
        }
    } else {
        ip!!
    }
}