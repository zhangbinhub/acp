package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.spring.cloud.conf.AcpCloudLogServerClientConfiguration
import pers.acp.spring.cloud.conf.AcpCloudLogServerConfiguration
import pers.acp.spring.cloud.log.LogConstant
import pers.acp.spring.cloud.log.producer.LogBridge
import pers.acp.spring.cloud.log.producer.StreamLogBridge

import javax.annotation.PostConstruct

/**
 * 日志服务客户端自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("'\${acp.cloud.log-server.client.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration::class, AcpCloudLogAutoConfiguration::class)
class AcpCloudLogServerClientAutoConfiguration @Autowired
constructor(
    private val acpCloudLogServerConfiguration: AcpCloudLogServerConfiguration,
    private val acpCloudLogServerClientConfiguration: AcpCloudLogServerClientConfiguration,
    private val bindingServiceProperties: BindingServiceProperties
) {
    /**
     * 初始化日志消息生产者
     */
    @PostConstruct
    fun init() {
        if (acpCloudLogServerClientConfiguration.enabled) {
            if (this.bindingServiceProperties.bindings[LogConstant.OUTPUT] == null) {
                this.bindingServiceProperties.bindings[LogConstant.OUTPUT] = BindingProperties()
            }
            this.bindingServiceProperties.bindings[LogConstant.OUTPUT]?.let {
                if (it.destination == null || it.destination == LogConstant.OUTPUT) {
                    it.destination = acpCloudLogServerConfiguration.destination
                }
                it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(LogBridge::class)
    fun streamLogBridge(streamBridge: StreamBridge, objectMapper: ObjectMapper): LogBridge =
        StreamLogBridge(streamBridge, objectMapper, LogConstant.OUTPUT)
}
