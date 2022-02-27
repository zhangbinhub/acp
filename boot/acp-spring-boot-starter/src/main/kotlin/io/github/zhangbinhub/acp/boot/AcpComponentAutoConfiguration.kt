package io.github.zhangbinhub.acp.boot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.zhangbinhub.acp.boot.aspect.RestControllerAspect
import io.github.zhangbinhub.acp.boot.component.FileDownLoadHandle
import io.github.zhangbinhub.acp.boot.component.ServerTools
import io.github.zhangbinhub.acp.boot.conf.ControllerLogConfiguration
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.tools.PackageTools
import io.github.zhangbinhub.acp.boot.tools.SpringBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpComponentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpringBeanFactory::class)
    fun springBeanFactory() = SpringBeanFactory()

    @Primary
    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    @ConditionalOnBean(JacksonProperties::class)
    fun jacksonObjectMapper(jacksonProperties: JacksonProperties): ObjectMapper =
        PackageTools.buildJacksonObjectMapper(jacksonProperties).apply {
            try {
                Class.forName("com.fasterxml.jackson.module.kotlin.KotlinModule")?.also {
                    this.registerKotlinModule()
                }
            } catch (e: Throwable) {
            }
        }

    @Bean
    fun restControllerAspect(
        controllerLogConfiguration: ControllerLogConfiguration,
        objectMapper: ObjectMapper
    ) = RestControllerAspect(controllerLogConfiguration, objectMapper)

    @Bean
    @ConditionalOnMissingBean(FileDownLoadHandle::class)
    fun fileDownLoadHandle(logAdapter: LogAdapter) = FileDownLoadHandle(logAdapter)

    @Bean
    fun serverTools() = ServerTools()

}