package pers.acp.spring.cloud.log.consumer

import pers.acp.core.CommonTools
import pers.acp.core.exceptions.EnumValueUndefinedException
import pers.acp.core.log.LogFactory
import pers.acp.core.task.BaseAsyncTask
import pers.acp.core.task.threadpool.ThreadPoolService
import pers.acp.spring.cloud.enums.LogLevel
import pers.acp.spring.cloud.log.LogInfo

/**
 * 默认日志消息处理类
 * 如需自定义，请实现 LogProcess 接口
 *
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
class DefaultLogProcess : LogProcess {

    override fun process(logInfo: LogInfo) {
        // 每个日志类型启动一个线程池，池中仅有一个线程，保证每个类型的消息顺序处理
        val threadPoolService = ThreadPoolService.getInstance(1, 1, Int.MAX_VALUE, logInfo.logType + "_log")
        threadPoolService.addTask(object : BaseAsyncTask(logInfo.logType + "_log", false) {
            override fun beforeExecuteFun(): Boolean = true
            override fun executeFun(): Any {
                doLog(logInfo)
                return true
            }

            override fun afterExecuteFun(result: Any) {}
        })
    }

    private fun doLog(logInfo: LogInfo) {
        val logFactory = LogFactory.getInstance(logInfo.logType)
        val logLevel: LogLevel = try {
            LogLevel.getEnum(logInfo.logLevel!!)
        } catch (e: EnumValueUndefinedException) {
            logFactory.error(e.message, e)
            LogLevel.Other
        }
        val message = StringBuilder()
                .append("[ ").append(CommonTools.strFillIn(logLevel.name, 5, 1, " ")).append(" ] ")
                .append("[").append(logInfo.serverName).append("] ")
                .append("[").append(logInfo.getServerIp()).append("] ")
                .append("[").append(logInfo.serverPort).append("] ")
                .append("[").append(logInfo.className).append("] ")
                .append("[ ").append(logInfo.lineno).append(" ] - ")
                .append(logInfo.message)
        when (logLevel) {
            LogLevel.Debug -> {
                if (logInfo.params.isNotEmpty()) {
                    logFactory.debug(message.toString(), *logInfo.params.toTypedArray())
                } else {
                    logFactory.debug(message.toString())
                }
            }
            LogLevel.Warn -> {
                if (logInfo.params.isNotEmpty()) {
                    logFactory.warn(message.toString(), *logInfo.params.toTypedArray())
                } else {
                    logFactory.warn(message.toString())
                }
            }
            LogLevel.Error -> {
                if (logInfo.params.isNotEmpty()) {
                    logFactory.error(message.toString(), *logInfo.params.toTypedArray())
                } else {
                    logFactory.error(message.toString())
                }
            }
            LogLevel.Trace -> {
                if (logInfo.params.isNotEmpty()) {
                    logFactory.trace(message.toString(), *logInfo.params.toTypedArray())
                } else {
                    logFactory.trace(message.toString())
                }
            }
            else -> {
                if (logInfo.params.isNotEmpty()) {
                    logFactory.info(message.toString(), *logInfo.params.toTypedArray())
                } else {
                    logFactory.info(message.toString())
                }
            }
        }
    }

}
