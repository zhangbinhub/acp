package com.github.zhangbinhub.acp.cloud.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import com.github.zhangbinhub.acp.cloud.log.LogConstant

/**
 * 日志服务客户端配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.cloud.log-server.client")
class AcpCloudLogServerClientConfiguration {

    /**
     * 是否启用日志服务客户端，默认：false
     */
    var enabled = false

    /**
     * 日志类型，默认："ALL"
     */
    var logType = LogConstant.DEFAULT_TYPE

}
