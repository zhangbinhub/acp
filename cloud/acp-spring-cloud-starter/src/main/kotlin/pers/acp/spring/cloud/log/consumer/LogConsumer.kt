package pers.acp.spring.cloud.log.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.stream.annotation.StreamListener
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.cloud.log.LogConstant
import pers.acp.spring.cloud.log.LogInfo

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
class LogConsumer(private val objectMapper: ObjectMapper, private val logProcess: LogProcess) {

    private val log = LogFactory.getInstance(LogConsumer::class.java)

    @StreamListener(LogConstant.INPUT)
    fun consumer(message: String) {
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
