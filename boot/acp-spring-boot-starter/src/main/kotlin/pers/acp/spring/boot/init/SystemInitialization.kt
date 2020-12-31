package pers.acp.spring.boot.init

import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.spring.boot.component.SystemControl
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.interfaces.LogAdapter

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
