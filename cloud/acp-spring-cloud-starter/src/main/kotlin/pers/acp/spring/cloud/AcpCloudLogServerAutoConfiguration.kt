package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.cloud.stream.function.StreamFunctionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.spring.cloud.conf.AcpCloudLogServerConfiguration
import pers.acp.spring.cloud.log.consumer.DefaultLogProcess
import pers.acp.spring.cloud.log.consumer.LogConsumer
import pers.acp.spring.cloud.log.consumer.LogProcess
import pers.acp.spring.cloud.log.LogConstant

import javax.annotation.PostConstruct

/**
 * 日志服务自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("'\${acp.cloud.log-server.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration::class)
class AcpCloudLogServerAutoConfiguration @Autowired
constructor(
    private val acpCloudLogServerConfiguration: AcpCloudLogServerConfiguration,
    private val bindingServiceProperties: BindingServiceProperties,
    private val streamFunctionProperties: StreamFunctionProperties
) {
    private val logConsumerBindName = "${LogConstant.INPUT}-in-0"

    /**
     * 初始化日志消息消费者
     */
    @PostConstruct
    fun init() {
        if (acpCloudLogServerConfiguration.enabled) {
            if (this.bindingServiceProperties.bindings[logConsumerBindName] == null) {
                this.bindingServiceProperties.bindings[logConsumerBindName] = BindingProperties()
            }
            if (this.streamFunctionProperties.definition != null && this.streamFunctionProperties.definition.isNotBlank()) {
                this.streamFunctionProperties.definition += ";${LogConstant.INPUT}"
            } else {
                this.streamFunctionProperties.definition = LogConstant.INPUT
            }
            this.bindingServiceProperties.bindings[logConsumerBindName]?.let {
                if (it.destination == null || it.destination == logConsumerBindName) {
                    it.destination = acpCloudLogServerConfiguration.destination
                }
                it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
                it.group = acpCloudLogServerConfiguration.groupId
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(LogProcess::class)
    fun logProcess(): LogProcess = DefaultLogProcess()

    @Bean(LogConstant.INPUT)
    fun logConsumer(objectMapper: ObjectMapper, logProcess: LogProcess): LogConsumer {
        return LogConsumer(objectMapper, logProcess)
    }
}
