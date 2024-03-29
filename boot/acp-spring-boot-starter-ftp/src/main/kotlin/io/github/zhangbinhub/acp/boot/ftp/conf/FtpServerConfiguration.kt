package io.github.zhangbinhub.acp.boot.ftp.conf

import io.github.zhangbinhub.acp.core.ftp.InitFtpServer
import io.github.zhangbinhub.acp.core.ftp.conf.FtpListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Ftp 服务端配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@ConditionalOnClass(InitFtpServer::class)
@ConfigurationProperties(prefix = "acp.ftp-server")
class FtpServerConfiguration {

    /**
     * Ftp服务监听配置列表
     */
    var listeners: MutableList<FtpListener> = mutableListOf()

}
