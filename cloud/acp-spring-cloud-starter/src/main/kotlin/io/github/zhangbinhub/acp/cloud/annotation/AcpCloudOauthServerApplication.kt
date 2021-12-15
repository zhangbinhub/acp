package io.github.zhangbinhub.acp.cloud.annotation

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.lang.annotation.Inherited

/**
 * Cloud 鉴权服务注解
 *
 * @author zhang by 14/01/2019 16:13
 * @since JDK 11
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
@EnableKnife4j
annotation class AcpCloudOauthServerApplication
