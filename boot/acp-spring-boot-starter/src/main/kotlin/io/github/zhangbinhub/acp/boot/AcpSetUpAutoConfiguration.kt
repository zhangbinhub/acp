package io.github.zhangbinhub.acp.boot

import io.github.zhangbinhub.acp.boot.base.BaseInitialization
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.listener.AcpApplicationStartupListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpSetUpAutoConfiguration {
    @Bean
    fun acpApplicationStartupListener(logAdapter: LogAdapter, initializationMap: Map<String, BaseInitialization>) =
        AcpApplicationStartupListener(logAdapter, initializationMap)
}