package io.github.zhangbinhub.acp.cloud

import io.github.zhangbinhub.acp.boot.component.ServerTools
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import org.springframework.cloud.commons.util.InetUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpCloudToolsAutoConfiguration {
    @Bean
    fun cloudTools(serverTools: ServerTools, inetUtils: InetUtils) = CloudTools(serverTools, inetUtils)
}
