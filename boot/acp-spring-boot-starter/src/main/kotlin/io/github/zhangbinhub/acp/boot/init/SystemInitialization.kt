package io.github.zhangbinhub.acp.boot.init

import io.github.zhangbinhub.acp.boot.base.BaseInitialization
import io.github.zhangbinhub.acp.boot.component.SystemControl
import io.github.zhangbinhub.acp.boot.daemon.DaemonServiceManager
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * 系统初始化
 * Created by zhangbin on 2017-6-16.
 */
class SystemInitialization(private val logAdapter: LogAdapter,
                           private val systemControl: SystemControl,
                           private val initServer: InitServer) : BaseInitialization() {

    override val name: String
        get() = "System Initialization"

    override val order: Int
        get() = 0

    override fun start() {
        logAdapter.info(">>>>>>>>>>>>>>>>>>>> system is starting ...")
        /* 启动初始化服务 */
        initServer.start()
        /* 启动 listener 及定时任务 */
        systemControl.initialization()
        Runtime.getRuntime().addShutdownHook(Thread { DaemonServiceManager.stopAllService() })
        logAdapter.info(">>>>>>>>>>>>>>>>>>>> system start finished!")
    }

    override fun stop() {
        systemControl.stop()
    }
}
