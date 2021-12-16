package io.github.zhangbinhub.acp.cloud

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler


/**
 * @author zhang by 10/09/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
class AcpCloudMethodSecurityAutoConfiguration : GlobalMethodSecurityConfiguration() {
    override fun createExpressionHandler() = OAuth2MethodSecurityExpressionHandler()
}