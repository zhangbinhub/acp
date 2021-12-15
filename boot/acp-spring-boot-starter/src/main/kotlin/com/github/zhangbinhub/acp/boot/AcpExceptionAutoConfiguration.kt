package com.github.zhangbinhub.acp.boot

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import com.github.zhangbinhub.acp.boot.exceptions.RestExceptionHandler
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * @author zhang by 31/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpExceptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ResponseEntityExceptionHandler::class)
    fun restExceptionHandler(logAdapter: LogAdapter) = RestExceptionHandler(logAdapter)

}