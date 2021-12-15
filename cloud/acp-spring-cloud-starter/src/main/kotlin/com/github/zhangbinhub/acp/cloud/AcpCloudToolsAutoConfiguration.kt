package com.github.zhangbinhub.acp.cloud

import org.springframework.cloud.commons.util.InetUtils
import org.springframework.context.annotation.*
import com.github.zhangbinhub.acp.boot.component.ServerTools
import com.github.zhangbinhub.acp.cloud.component.CloudTools

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpCloudToolsAutoConfiguration {
    @Bean
    fun cloudTools(serverTools: ServerTools, inetUtils: InetUtils) = CloudTools(serverTools, inetUtils)
}
