package io.github.zhangbinhub.acp.boot.ftp.conf

import io.github.zhangbinhub.acp.core.ftp.InitSftpServer
import io.github.zhangbinhub.acp.core.ftp.conf.SftpListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Sftp 服务配置
 *
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@ConditionalOnClass(InitSftpServer::class)
@ConfigurationProperties(prefix = "acp.sftp-server")
class SftpServerConfiguration {

    /**
     * Sftp 服务监听列表
     */
    var listeners: MutableList<SftpListener> = mutableListOf()

}
