package com.github.zhangbinhub.acp.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.github.zhangbinhub.acp.cloud.log.LogConstant
import com.github.zhangbinhub.acp.cloud.log.producer.LogBridge
import com.github.zhangbinhub.acp.cloud.log.producer.StreamLogBridge

/**
 * 日志服务客户端自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("'\${acp.cloud.log-server.client.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration::class, AcpCloudLogAutoConfiguration::class)
class AcpCloudLogServerClientAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(LogBridge::class)
    fun streamLogBridge(streamBridge: StreamBridge, objectMapper: ObjectMapper): LogBridge =
        StreamLogBridge(streamBridge, objectMapper, LogConstant.OUTPUT)
}
