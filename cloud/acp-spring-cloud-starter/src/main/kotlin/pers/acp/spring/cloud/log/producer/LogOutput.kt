package pers.acp.spring.cloud.log.producer

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import pers.acp.spring.cloud.log.LogConstant

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
interface LogOutput {

    @Output(LogConstant.OUTPUT)
    fun sendMessage(): MessageChannel

}
