package com.github.zhangbinhub.acp.cloud.annotation

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import java.lang.annotation.Inherited

/**
 * Cloud 原子服务注解
 *
 * @author zhangbin by 12/04/2018 12:47
 * @since JDK 11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@AcpCloudOauthServerApplication
@EnableOAuth2Sso
annotation class AcpCloudAtomApplication
