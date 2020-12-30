package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.spring.boot.AcpBootLogAutoConfiguration
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.conf.AcpCloudLogServerClientConfiguration
import pers.acp.spring.cloud.log.CloudLogAdapter
import pers.acp.spring.cloud.log.producer.LogBridge

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
    fun logAdapter(acpCloudLogServerClientConfiguration: AcpCloudLogServerClientConfiguration, logBridge: LogBridge) =
        CloudLogAdapter(acpCloudLogServerClientConfiguration, logBridge)
}