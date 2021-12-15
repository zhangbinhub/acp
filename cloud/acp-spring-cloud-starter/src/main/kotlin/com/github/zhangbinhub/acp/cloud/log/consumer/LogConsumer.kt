package com.github.zhangbinhub.acp.cloud.log.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.zhangbinhub.acp.core.CommonTools
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.cloud.log.LogConstant
import com.github.zhangbinhub.acp.cloud.log.LogInfo
import java.util.function.Consumer

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
class LogConsumer(private val objectMapper: ObjectMapper, private val logProcess: LogProcess) :
    Consumer<String> {

    private val log = LogFactory.getInstance(LogConsumer::class.java)

    override fun accept(message: String) {
        try {
            val logInfo = objectMapper.readValue(message, LogInfo::class.java)
            var logType = LogConstant.DEFAULT_TYPE
            if (!CommonTools.isNullStr(logInfo.logType)) {
                logType = logInfo.logType
            }
            logInfo.logType = logType
            logProcess.process(logInfo)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
