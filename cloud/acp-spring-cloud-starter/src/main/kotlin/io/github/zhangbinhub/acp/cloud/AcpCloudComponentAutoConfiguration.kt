package io.github.zhangbinhub.acp.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.zhangbinhub.acp.cloud.aspect.RestControllerRepeatAspect
import io.github.zhangbinhub.acp.cloud.error.AuthAccessDeniedHandler
import io.github.zhangbinhub.acp.cloud.error.AuthExceptionEntryPoint
import io.github.zhangbinhub.acp.cloud.lock.DistributedLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
