package pers.acp.spring.boot.init.task

import pers.acp.core.CommonTools
import pers.acp.spring.boot.conf.UdpServerConfiguration
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.init.BaseInitTask
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.socket.udp.UdpServer
import pers.acp.spring.boot.socket.base.ISocketServerHandle

/**
 * 初始化UDP服务
 */
class InitUdpServer(private val logAdapter: LogAdapter,
                    private val udpServerConfiguration: UdpServerConfiguration,
                    private val socketServerHandleList: List<ISocketServerHandle>) : BaseInitTask() {

    fun startUdpServer() {
        logAdapter.info("start udp listen service ...")
        if (socketServerHandleList.isNotEmpty()) {
            socketServerHandleList.forEach { socketServerHandle -> addServerHandle(socketServerHandle) }
        }
        try {
            val listens = udpServerConfiguration.listeners
            if (listens.isNotEmpty()) {
                listens.forEach { listen ->
                    run {
                        if (listen.enabled) {
                            val beanName = listen.handleBean
                            if (!CommonTools.isNullStr(beanName)) {
                                val handle = getSocketServerHandle(beanName)
                                if (handle != null) {
                                    val port = listen.port
                                    val server = UdpServer(logAdapter, port, listen, handle)
                                    val sub = Thread(server)
                                    sub.isDaemon = true
                                    sub.start()
                                    DaemonServiceManager.addService(server)
                                    logAdapter.info("start udp listen service Success [" + listen.name + "] , port:" + listen.port)
                                } else {
                                    logAdapter.error("udp handle bean [$beanName] is invalid!")
                                }
                            }
                        } else {
                            logAdapter.info("udp listen service is disabled [" + listen.name + "]")
                        }
                    }
                }
            } else {
                logAdapter.info("No udp listen service was found")
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        } finally {
            logAdapter.info("start udp listen service finished!")
        }
    }

}
