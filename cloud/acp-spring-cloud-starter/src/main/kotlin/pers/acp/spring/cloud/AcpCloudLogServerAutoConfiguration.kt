package pers.acp.spring.cloud

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.spring.cloud.log.consumer.DefaultLogProcess
import pers.acp.spring.cloud.log.consumer.LogConsumer
import pers.acp.spring.cloud.log.consumer.LogProcess
import pers.acp.spring.cloud.log.LogConstant

/**
 * 日志服务自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("'\${acp.cloud.log-server.enabled}'.equals('true')")
@AutoConfigureBefore(BindingServiceConfiguration::class)
class AcpCloudLogServerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(LogProcess::class)
    fun logProcess(): LogProcess = DefaultLogProcess()

    @Bean(LogConstant.CONSUMER)
    @ConditionalOnMissingBean(name = [LogConstant.CONSUMER])
    fun logConsumer(objectMapper: ObjectMapper, logProcess: LogProcess): LogConsumer {
        return LogConsumer(objectMapper, logProcess)
    }
}
