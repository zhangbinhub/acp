package io.github.zhangbinhub.acp.cloud.log

import com.fasterxml.jackson.core.JsonProcessingException
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.task.BaseAsyncTask
import io.github.zhangbinhub.acp.core.task.threadpool.ThreadPoolService
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.tools.SpringBeanFactory
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import io.github.zhangbinhub.acp.cloud.enums.LogLevel
import io.github.zhangbinhub.acp.cloud.conf.AcpCloudLogServerClientConfiguration
import io.github.zhangbinhub.acp.cloud.log.producer.LogBridge

/**
 * 日志实例
 *
 * @author zhangbin by 11/07/2018 13:36
 * @since JDK 11
 */
class CloudLogAdapter(
    private val cloudTools: CloudTools,
    private val acpCloudLogServerClientConfiguration: AcpCloudLogServerClientConfiguration,
    private val logBridge: LogBridge
) : LogAdapter {
    private val stackIndex = 4
    private fun logInstance(): LogFactory =
        LogFactory.getInstance(Thread.currentThread().stackTrace[stackIndex - 1].className, stackIndex)

    private fun generateLogInfo(): LogInfo? {
        val logInfo = SpringBeanFactory.getBean(LogInfo::class.java)
        logInfo?.let {
            var logType = acpCloudLogServerClientConfiguration.logType
            if (CommonTools.isNullStr(logType)) {
                logType = LogConstant.DEFAULT_TYPE
            }
            it.logType = logType
            it.serverIp = cloudTools.getServerIp()
            it.serverPort = cloudTools.getServerPort()
        }
        return logInfo
    }

    private fun sendToLogServer(logInfo: LogInfo) {
        val stacks = Thread.currentThread().stackTrace
        // 启动一个线程池，池中仅有一个线程，保证每个日志消息顺序处理
        val threadPoolService = ThreadPoolService.getInstance(1, 1, Int.MAX_VALUE, "cloud_log_adapter_thread_pool")
        threadPoolService.addTask(object : BaseAsyncTask("cloud_log_adapter_thread_task", false) {
            override fun beforeExecuteFun(): Boolean = true
            override fun afterExecuteFun(result: Any) {}
            override fun executeFun(): Any {
                logInfo.serverTime = CommonTools.getNowDateTime().toDate().time
                logInfo.lineNo = stacks[stackIndex - 1].lineNumber
                logInfo.className = stacks[stackIndex - 1].className
                try {
                    if (acpCloudLogServerClientConfiguration.enabled) {
                        logBridge.send(logInfo)
                    }
                } catch (e: JsonProcessingException) {
                    logInstance().error(e.message, e)
                }
                return true
            }
        })
    }

    override fun info(message: String?) {
        logInstance().let { log ->
            log.info(message)
            if (log.isInfoEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Info.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun info(message: String?, vararg variable: Any?) {
        logInstance().let { log ->
            log.info(message, *variable)
            if (log.isInfoEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Info.name
                    it.message = message
                    it.params = arrayListOf(*variable)
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun info(message: String?, t: Throwable?) {
        logInstance().let { log ->
            log.info(message, t)
            if (log.isInfoEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Info.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun debug(message: String?) {
        logInstance().let { log ->
            log.debug(message)
            if (log.isDebugEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Debug.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun debug(message: String?, vararg variable: Any?) {
        logInstance().let { log ->
            log.debug(message, *variable)
            if (log.isDebugEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Debug.name
                    it.message = message
                    it.params = arrayListOf(*variable)
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun debug(message: String?, t: Throwable?) {
        logInstance().let { log ->
            log.debug(message, t)
            if (log.isDebugEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Debug.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun warn(message: String?) {
        logInstance().let { log ->
            log.warn(message)
            if (log.isWarnEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Warn.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun warn(message: String?, vararg variable: Any?) {
        logInstance().let { log ->
            log.warn(message, *variable)
            if (log.isWarnEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Warn.name
                    it.message = message
                    it.params = arrayListOf(*variable)
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun warn(message: String?, t: Throwable?) {
        logInstance().let { log ->
            log.warn(message, t)
            if (log.isWarnEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Warn.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun error(message: String?) {
        logInstance().let { log ->
            log.error(message)
            if (log.isErrorEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Error.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun error(message: String?, vararg variable: Any?) {
        logInstance().let { log ->
            log.error(message, *variable)
            if (log.isErrorEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Error.name
                    it.message = message
                    it.params = arrayListOf(*variable)
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun error(message: String?, t: Throwable?) {
        logInstance().let { log ->
            log.error(message, t)
            if (log.isErrorEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Error.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun trace(message: String?) {
        logInstance().let { log ->
            log.trace(message)
            if (log.isTraceEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Trace.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun trace(message: String?, vararg variable: Any?) {
        logInstance().let { log ->
            log.trace(message, *variable)
            if (log.isTraceEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Trace.name
                    it.message = message
                    it.params = arrayListOf(*variable)
                    sendToLogServer(it)
                }
            }
        }
    }

    override fun trace(message: String?, t: Throwable?) {
        logInstance().let { log ->
            log.trace(message, t)
            if (log.isTraceEnabled()) {
                val logInfo = generateLogInfo()
                logInfo?.let {
                    it.logLevel = LogLevel.Trace.name
                    it.message = message
                    sendToLogServer(it)
                }
            }
        }
    }

}
