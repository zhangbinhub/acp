package pers.acp.spring.boot

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pers.acp.spring.boot.exceptions.RestExceptionHandler
import pers.acp.spring.boot.interfaces.LogAdapter

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