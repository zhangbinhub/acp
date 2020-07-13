package pers.acp.spring.cloud.log.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel
import pers.acp.spring.cloud.log.LogConstant

/**
 * @author zhangbin by 11/07/2018 14:34
 * @since JDK 11
 */
interface LogInput {

    @Input(LogConstant.INPUT)
    fun input(): SubscribableChannel

}
