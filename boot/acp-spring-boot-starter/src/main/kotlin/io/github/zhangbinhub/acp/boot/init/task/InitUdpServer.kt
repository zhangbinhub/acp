package io.github.zhangbinhub.acp.boot.init.task

import io.github.zhangbinhub.acp.boot.conf.UdpServerConfiguration
import io.github.zhangbinhub.acp.boot.daemon.DaemonServiceManager
import io.github.zhangbinhub.acp.boot.init.BaseInitTask
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle
import io.github.zhangbinhub.acp.boot.socket.udp.UdpServer
import io.github.zhangbinhub.acp.core.CommonTools

/**
 * 初始化UDP服务
 */
class InitUdpServer(
    private val logAdapter: LogAdapter,
    private val udpServerConfiguration: UdpServerConfiguration,
    private val socketServerHandleList: List<ISocketServerHandle>
) : BaseInitTask() {

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
