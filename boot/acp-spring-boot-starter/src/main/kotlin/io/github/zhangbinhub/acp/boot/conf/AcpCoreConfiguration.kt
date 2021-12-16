package io.github.zhangbinhub.acp.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 核心配置信息
 *
 * @author zhang by 20/06/2019
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.core")
class AcpCoreConfiguration {

    /**
     * 延迟删除文件等待时间（单位毫秒），默认：1200000
     */
    var deleteFileWaitTime: Long = 1200000

    /**
     * 绝对路径前缀，默认："abs:"
     */
    var absPathPrefix = "abs:"

    /**
     * 用户路径前缀，默认："user:"
     */
    var userPathPrefix = "user:"

    /**
     * 字体文件夹路径，默认："files/resource/font"
     */
    var fontPath = "files/resource/font"

}
