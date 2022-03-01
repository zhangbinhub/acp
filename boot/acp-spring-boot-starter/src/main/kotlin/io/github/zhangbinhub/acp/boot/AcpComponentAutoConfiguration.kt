package io.github.zhangbinhub.acp.boot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.zhangbinhub.acp.boot.aspect.RestControllerAspect
import io.github.zhangbinhub.acp.boot.base.BaseSpringBootScheduledAsyncTask
import io.github.zhangbinhub.acp.boot.component.FileDownLoadHandle
import io.github.zhangbinhub.acp.boot.component.ServerTools
import io.github.zhangbinhub.acp.boot.component.SystemControl
import io.github.zhangbinhub.acp.boot.component.TimerTaskSchedulerCtrl
import io.github.zhangbinhub.acp.boot.conf.*
import io.github.zhangbinhub.acp.boot.init.InitServer
import io.github.zhangbinhub.acp.boot.init.SystemInitialization
import io.github.zhangbinhub.acp.boot.init.task.InitTcpServer
import io.github.zhangbinhub.acp.boot.init.task.InitUdpServer
import io.github.zhangbinhub.acp.boot.interfaces.Listener
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle
import io.github.zhangbinhub.acp.boot.tools.PackageTools
import io.github.zhangbinhub.acp.boot.tools.SpringBeanFactory
import io.netty.handler.codec.ByteToMessageDecoder
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.util.ReflectionUtils
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpComponentAutoConfiguration {
    @Bean
    fun systemInitialization(
        logAdapter: LogAdapter,
        listenerMap: Map<String, Listener>?,
        properties: TaskSchedulingProperties,
        scheduleConfiguration: ScheduleConfiguration,
        baseSpringBootScheduledTaskMap: Map<String, BaseSpringBootScheduledAsyncTask>,
        acpCoreConfiguration: AcpCoreConfiguration,
        tcpServerConfiguration: TcpServerConfiguration,
        udpServerConfiguration: UdpServerConfiguration,
        socketServerHandleList: List<ISocketServerHandle>,
        byteToMessageDecoderList: List<ByteToMessageDecoder>
    ) = SystemInitialization(
        logAdapter,
        SystemControl(
            logAdapter,
            listenerMap,
            TimerTaskSchedulerCtrl(logAdapter, properties, scheduleConfiguration, baseSpringBootScheduledTaskMap)
        ), InitServer(
            logAdapter,
            InitTcpServer(logAdapter, tcpServerConfiguration, socketServerHandleList, byteToMessageDecoderList),
            InitUdpServer(logAdapter, udpServerConfiguration, socketServerHandleList),
            acpCoreConfiguration
        )
    )

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

    /**
     * 增加此配置，修复springfox在spring boot 2.6.x版本下空指针异常
     */
    @Bean
    @ConditionalOnClass(WebMvcRequestHandlerProvider::class, WebFluxRequestHandlerProvider::class)
    fun springfoxHandlerProviderBeanPostProcessor(): BeanPostProcessor {
        return object : BeanPostProcessor {
            @Throws(BeansException::class)
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
                if (bean is WebMvcRequestHandlerProvider || bean is WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean))
                }
                return bean
            }

            private fun customizeSpringfoxHandlerMappings(mappings: Any?) {
                if (mappings is ArrayList<*>) {
                    mappings.removeIf { mapping ->
                        if (mapping is RequestMappingInfoHandlerMapping) {
                            mapping.patternParser != null
                        } else {
                            false
                        }
                    }
                }
            }

            private fun getHandlerMappings(bean: Any): Any = try {
                ReflectionUtils.findField(bean.javaClass, "handlerMappings")?.let {
                    it.isAccessible = true
                    it[bean]
                } ?: mutableListOf<RequestMappingInfoHandlerMapping>()
            } catch (e: IllegalArgumentException) {
                throw IllegalStateException(e)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(e)
            }
        }
    }
}