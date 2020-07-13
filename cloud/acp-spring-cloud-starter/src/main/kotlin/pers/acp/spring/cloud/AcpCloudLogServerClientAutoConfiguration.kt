package pers.acp.spring.cloud

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.spring.cloud.conf.AcpCloudLogServerClientConfiguration
import pers.acp.spring.cloud.conf.AcoCloudLogServerConfiguration
import pers.acp.spring.cloud.log.producer.LogOutput
import pers.acp.spring.cloud.log.producer.LogProducer
import pers.acp.spring.cloud.log.LogConstant

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
@EnableBinding(LogOutput::class)
class AcpCloudLogServerClientAutoConfiguration @Autowired
constructor(private val acoCloudLogServerConfiguration: AcoCloudLogServerConfiguration,
            private val acpCloudLogServerClientConfiguration: AcpCloudLogServerClientConfiguration,
            private val bindings: BindingServiceProperties) {

    /**
     * 初始化日志消息生产者
     */
    @PostConstruct
    fun init() {
        if (acpCloudLogServerClientConfiguration.enabled) {
            if (this.bindings.bindings[LogConstant.OUTPUT] == null) {
                this.bindings.bindings[LogConstant.OUTPUT] = BindingProperties()
            }
            this.bindings.bindings[LogConstant.OUTPUT]?.let {
                if (it.destination == null || it.destination == LogConstant.OUTPUT) {
                    it.destination = acoCloudLogServerConfiguration.destination
                }
                it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            }
        }
    }

    @Bean
    fun logProducer(logOutput: LogOutput): LogProducer = LogProducer(logOutput)

}
