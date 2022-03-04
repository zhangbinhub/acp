package io.github.zhangbinhub.acp.boot

import io.github.zhangbinhub.acp.boot.actuate.info.AcpBootInfoContributor
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(EndpointAutoConfiguration::class)
@ConditionalOnClass(InfoContributor::class, ConditionalOnEnabledInfoContributor::class)
class AcpBootInfoAutoConfiguration {
    @Bean
    @ConditionalOnEnabledInfoContributor("acp")
    fun acpInfoContributor() = AcpBootInfoContributor()
}