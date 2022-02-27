package io.github.zhangbinhub.acp.boot.init

import io.github.zhangbinhub.acp.boot.conf.AcpCoreConfiguration
import io.github.zhangbinhub.acp.boot.init.task.InitTcpServer
import io.github.zhangbinhub.acp.boot.init.task.InitUdpServer
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.core.CommonTools

/**
 * 初始化系统及TCP、UDP服务
 */
class InitServer(
    private val logAdapter: LogAdapter,
    private val initTcpServer: InitTcpServer,
    private val initUdpServer: InitUdpServer,
    private val acpCoreConfiguration: AcpCoreConfiguration
) {

    /**
     * 主线程中进行系统初始化
     */
    internal fun start() {
        try {
            initTcpServer.startTcpServer()
            initUdpServer.startUdpServer()
            initTools()
        } catch (e: Exception) {
            logAdapter.error("system startup Exception:" + e.message)
        }

    }

    private fun initTools() {
        logAdapter.info("tools init begin ...")
        CommonTools.initTools(
            acpCoreConfiguration.deleteFileWaitTime,
            acpCoreConfiguration.absPathPrefix,
            acpCoreConfiguration.userPathPrefix,
            acpCoreConfiguration.fontPath
        )
        logAdapter.info("tools init finished!")
    }
}