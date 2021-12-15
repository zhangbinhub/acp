package com.github.zhangbinhub.acp.cloud.log.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder
import com.github.zhangbinhub.acp.cloud.log.LogInfo

class StreamLogBridge(
    private val streamBridge: StreamBridge,
    private val objectMapper: ObjectMapper,
    private val bindName: String
) : LogBridge {
    override fun send(logInfo: LogInfo) {
        streamBridge.send(
            bindName,
            MessageBuilder.withPayload(objectMapper.writeValueAsString(logInfo)).build()
        )
    }
}