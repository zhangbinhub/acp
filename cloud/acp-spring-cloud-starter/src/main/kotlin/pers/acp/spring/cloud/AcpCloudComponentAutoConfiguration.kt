package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.*
import pers.acp.spring.cloud.aspect.RestControllerRepeatAspect
import pers.acp.spring.cloud.error.AuthAccessDeniedHandler
import pers.acp.spring.cloud.error.AuthExceptionEntryPoint
import pers.acp.spring.cloud.lock.DistributedLock

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
