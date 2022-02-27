package io.github.zhangbinhub.acp.cloud

import feign.Feign
import feign.RequestInterceptor
import io.github.zhangbinhub.acp.core.CommonTools
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * @author zhangbin
 * @date 2020-12-31
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Feign::class)
class AcpCloudFeignAutoConfiguration {

    /**
     * 自定义 Feign 请求拦截器，请求之前将 Oauth2 token 信息带入 Request 的 header 进行权限传递
     *
     * @return 自定义 Feign 请求拦截器实例
     */
    @Primary
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            // 获取当前服务的 request 对象，将 header 中的 Authorization 传递给 feign 的 request 对象
            RequestContextHolder.getRequestAttributes()?.let {
                if (it is ServletRequestAttributes) {
                    val request = it.request
                    val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
                    if (!CommonTools.isNullStr(authorization)) {
                        template.header(HttpHeaders.AUTHORIZATION, authorization)
                    }
                }
            }
        }
    }
}
