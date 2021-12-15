package com.github.zhangbinhub.acp.boot.ftp.conf

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties
import com.github.zhangbinhub.acp.core.ftp.InitFtpServer
import com.github.zhangbinhub.acp.core.ftp.conf.FtpListener

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
