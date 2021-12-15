package io.github.zhangbinhub.acp.cloud

import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.cloud.bus.*
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import io.github.zhangbinhub.acp.boot.component.BootLogAdapter

@Configuration(proxyBeanMethods = false)
@ConditionalOnBusEnabled
@EnableConfigurationProperties(BusProperties::class)
@AutoConfigureBefore(PathServiceMatcherAutoConfiguration::class)
class AcpCloudBusAutoConfiguration(
    private val contextRefresher: ContextRefresher
) : ApplicationListener<WebServerInitializedEvent> {
    private val logAdapter = BootLogAdapter()

    @BusPathMatcher // There is a @Bean of type PathMatcher coming from Spring MVC
    @ConditionalOnMissingBean(name = [BUS_PATH_MATCHER_NAME])
    @Bean(name = [BUS_PATH_MATCHER_NAME])
    fun busPathMatcher(): PathMatcher {
        return DefaultBusPathMatcher(AntPathMatcher(":"))
    }

    @Bean
    @RefreshScope // 解决动态端口时，spring.cloud.bus.id 无法识别是自身的问题
    @ConditionalOnMissingBean(ServiceMatcher::class)
    fun pathServiceMatcher(
        @BusPathMatcher pathMatcher: PathMatcher, properties: BusProperties,
        environment: Environment
    ): PathServiceMatcher {
        val configNames = environment.getProperty(
            CLOUD_CONFIG_NAME_PROPERTY,
            Array<String>::class.java, arrayOf()
        )
        return PathServiceMatcher(pathMatcher, properties.id, configNames)
    }

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        logAdapter.info("Web Server has started, reload the properties and refresh the bus id with server port: ${event.webServer.port}")
        contextRefresher.refresh()
    }

    companion object {
        const val BUS_PATH_MATCHER_NAME = "busPathMatcher"
        const val CLOUD_CONFIG_NAME_PROPERTY = "spring.cloud.config.name"
    }
}