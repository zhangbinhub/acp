package io.github.zhangbinhub.acp.boot

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.github.zhangbinhub.acp.boot.component.BootLogAdapter
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpBootLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogAdapter::class)
    fun logAdapter() = BootLogAdapter()

}