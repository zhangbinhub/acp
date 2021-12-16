package io.github.zhangbinhub.acp.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Swagger 配置
 *
 * @author zhang by 14/01/2019 15:07
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.swagger")
class SwaggerConfiguration {

    /**
     * 是否启用，默认：false
     */
    var enabled = false

}
