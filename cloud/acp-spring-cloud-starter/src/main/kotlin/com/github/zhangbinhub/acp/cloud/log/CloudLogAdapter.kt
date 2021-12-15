package com.github.zhangbinhub.acp.cloud.log

import com.fasterxml.jackson.core.JsonProcessingException
import com.github.zhangbinhub.acp.core.CommonTools
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.task.BaseAsyncTask
import com.github.zhangbinhub.acp.core.task.threadpool.ThreadPoolService
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import com.github.zhangbinhub.acp.boot.tools.SpringBeanFactory
import com.github.zhangbinhub.acp.cloud.component.CloudTools
import com.github.zhangbinhub.acp.cloud.enums.LogLevel
import com.github.zhangbinhub.acp.cloud.conf.AcpCloudLogServerClientConfiguration
import com.github.zhangbinhub.acp.cloud.log.producer.LogBridge

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

    private val log = LogFactory.getInstance(this.javaClass, 4)

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
                var lineNo = 0
                var className = ""
                if (stacks.size >= log.stackIndex) {
                    lineNo = stacks[log.stackIndex - 1].lineNumber
                    className = stacks[log.stackIndex - 1].className
                }
                logInfo.lineNo = lineNo
                logInfo.className = className
                try {
                    if (acpCloudLogServerClientConfiguration.enabled) {
                        logBridge.send(logInfo)
                    }
                } catch (e: JsonProcessingException) {
                    log.error(e.message, e)
                }
                return true
            }
        })
    }

    override fun info(message: String?) {
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

    override fun info(message: String?, vararg variable: Any?) {
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

    override fun info(message: String?, t: Throwable?) {
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

    override fun debug(message: String?) {
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

    override fun debug(message: String?, vararg variable: Any?) {
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

    override fun debug(message: String?, t: Throwable?) {
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

    override fun warn(message: String?) {
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

    override fun warn(message: String?, vararg variable: Any?) {
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

    override fun warn(message: String?, t: Throwable?) {
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

    override fun error(message: String?) {
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

    override fun error(message: String?, vararg variable: Any?) {
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

    override fun error(message: String?, t: Throwable?) {
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

    override fun trace(message: String?) {
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

    override fun trace(message: String?, vararg variable: Any?) {
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

    override fun trace(message: String?, t: Throwable?) {
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
