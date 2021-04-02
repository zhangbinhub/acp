package pers.acp.spring.boot.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import pers.acp.core.CommonTools
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
        logAdapter.info("Application was started, listener port: ${port.get()}")
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