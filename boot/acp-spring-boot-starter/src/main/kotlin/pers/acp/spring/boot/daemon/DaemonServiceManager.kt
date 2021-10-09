package pers.acp.spring.boot.daemon

import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.core.task.threadpool.ThreadPoolService

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Created by zhangbin on 2016/12/21.
 * 后台守护服务控制类
 */
class DaemonServiceManager : ServletContextListener {

    override fun contextInitialized(servletContextEvent: ServletContextEvent?) {
        // 服务器启动时执行初始化
    }

    override fun contextDestroyed(servletContextEvent: ServletContextEvent?) {
        stopAllService()
    }

    companion object {

        private val log = LogFactory.getInstance(DaemonServiceManager::class.java)

        private val serverDeque = ConcurrentLinkedDeque<IDaemonService>()

        /**
         * 添加后台守护服务
         *
         * @param daemonService 后台守护服务
         */
        @JvmStatic
        fun addService(daemonService: IDaemonService) {
            synchronized(serverDeque) {
                if (!serverDeque.contains(daemonService)) {
                    serverDeque.push(daemonService)
                    log.info("add daemon service [" + daemonService.getServiceName() + "]")
                }
            }
        }

        @JvmStatic
        fun addAllService(daemonServiceList: List<IDaemonService>) {
            synchronized(serverDeque) {
                daemonServiceList.forEach { daemonService ->
                    run {
                        if (!serverDeque.contains(daemonService)) {
                            serverDeque.push(daemonService)
                            log.info("add daemon service [" + daemonService.getServiceName() + "]")
                        }
                    }
                }
            }
        }

        /**
         * 停止后台守护服务
         */
        @JvmStatic
        fun stopAllService() {
            ThreadPoolService.destroyAll()
            synchronized(serverDeque) {
                while (!serverDeque.isEmpty()) {
                    val daemonService = serverDeque.pop()
                    daemonService.stopService()
                    log.info("destroy daemon service [" + daemonService.getServiceName() + "]")
                }
            }
        }
    }

}
