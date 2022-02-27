package io.github.zhangbinhub.acp.boot

import io.github.zhangbinhub.acp.boot.conf.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author zhang by 13/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
    AcpCoreConfiguration::class,
    ControllerLogConfiguration::class,
    ScheduleConfiguration::class,
    SwaggerConfiguration::class,
    TcpServerConfiguration::class,
    UdpServerConfiguration::class
)
class AcpPropertiesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JacksonProperties::class)
    fun jacksonProperties() = JacksonProperties()

}