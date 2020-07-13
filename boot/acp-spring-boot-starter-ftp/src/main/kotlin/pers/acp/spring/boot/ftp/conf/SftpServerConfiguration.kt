package pers.acp.spring.boot.ftp.conf

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties
import pers.acp.ftp.InitSftpServer
import pers.acp.ftp.conf.SftpListener

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
