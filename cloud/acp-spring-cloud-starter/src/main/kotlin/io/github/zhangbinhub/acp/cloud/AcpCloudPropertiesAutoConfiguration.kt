package io.github.zhangbinhub.acp.cloud

import io.github.zhangbinhub.acp.cloud.conf.AcpCloudLogServerClientConfiguration
import io.github.zhangbinhub.acp.cloud.conf.AcpCloudLogServerConfiguration
import io.github.zhangbinhub.acp.cloud.conf.AcpCloudOauthConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
    AcpCloudLogServerConfiguration::class,
    AcpCloudLogServerClientConfiguration::class,
    AcpCloudOauthConfiguration::class
)
@AutoConfigureAfter(OAuth2AutoConfiguration::class, FeignAutoConfiguration::class)
class AcpCloudPropertiesAutoConfiguration {

    /**
     * oauth2 客户端配置
     *
     * @return OAuth2ClientProperties
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(OAuth2ClientProperties::class)
    fun oAuth2ClientProperties(): OAuth2ClientProperties = OAuth2ClientProperties()

    /**
     * 获取服务配置信息
     *
     * @return ServerProperties
     */
    @Bean
    @ConditionalOnMissingBean(ServerProperties::class)
    fun serverProperties(): ServerProperties = ServerProperties()

    /**
     * 获取资源服务配置信息
     *
     * @return ResourceServerProperties
     */
    @Bean
    @ConditionalOnMissingBean(ResourceServerProperties::class)
    fun resourceServerProperties(): ResourceServerProperties = ResourceServerProperties()

    /**
     * feign客户端配置
     *
     * @return FeignHttpClientProperties
     */
    @Bean
    @ConditionalOnMissingBean(FeignHttpClientProperties::class)
    fun feignHttpClientProperties(): FeignHttpClientProperties = FeignHttpClientProperties()

}
