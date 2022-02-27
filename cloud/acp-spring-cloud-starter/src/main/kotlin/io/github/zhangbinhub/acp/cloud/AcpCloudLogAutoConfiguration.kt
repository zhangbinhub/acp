package io.github.zhangbinhub.acp.cloud

import io.github.zhangbinhub.acp.boot.AcpBootLogAutoConfiguration
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import io.github.zhangbinhub.acp.cloud.conf.AcpCloudLogServerClientConfiguration
import io.github.zhangbinhub.acp.cloud.log.CloudLogAdapter
import io.github.zhangbinhub.acp.cloud.log.LogInfo
import io.github.zhangbinhub.acp.cloud.log.producer.LogBridge
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("'\${acp.cloud.log-server.client.enabled}'.equals('true')")
@AutoConfigureBefore(AcpBootLogAutoConfiguration::class)
class AcpCloudLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogAdapter::class)
    fun logAdapter(
        cloudTools: CloudTools,
        acpCloudLogServerClientConfiguration: AcpCloudLogServerClientConfiguration,
        logBridge: LogBridge
    ) =
        CloudLogAdapter(cloudTools, acpCloudLogServerClientConfiguration, logBridge)

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun logInfo() = LogInfo()
}