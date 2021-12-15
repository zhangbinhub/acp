package com.github.zhangbinhub.acp.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.*
import com.github.zhangbinhub.acp.cloud.aspect.RestControllerRepeatAspect
import com.github.zhangbinhub.acp.cloud.error.AuthAccessDeniedHandler
import com.github.zhangbinhub.acp.cloud.error.AuthExceptionEntryPoint
import com.github.zhangbinhub.acp.cloud.lock.DistributedLock

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpCloudComponentAutoConfiguration {
    @Bean
    @ConditionalOnBean(DistributedLock::class)
    @Autowired(required = false)
    fun restControllerRepeatAspect(
        distributedLock: DistributedLock,
        objectMapper: ObjectMapper
    ) = RestControllerRepeatAspect(distributedLock, objectMapper)

    @Bean
    fun authAccessDeniedHandler(objectMapper: ObjectMapper) = AuthAccessDeniedHandler(objectMapper)

    @Bean
    fun authExceptionEntryPoint(objectMapper: ObjectMapper) = AuthExceptionEntryPoint(objectMapper)
}
