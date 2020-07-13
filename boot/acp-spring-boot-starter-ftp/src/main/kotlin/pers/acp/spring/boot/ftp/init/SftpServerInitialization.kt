package pers.acp.spring.boot.ftp.init

import pers.acp.ftp.InitSftpServer
import pers.acp.ftp.conf.SftpConfig
import pers.acp.ftp.user.UserFactory
import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.ftp.conf.SftpServerConfiguration

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class SftpServerInitialization(private val sftpServerConfiguration: SftpServerConfiguration,
                               private val userFactoryList: List<UserFactory>?) : BaseInitialization() {

    override val name: String
        get() = "sftp server setup server"

    override val order: Int
        get() = 1

    /**
     * 启动sftp服务
     */
    override fun start() {
        val sftpConfig = SftpConfig()
        sftpConfig.listens = sftpServerConfiguration.listeners
        DaemonServiceManager.addAllService(InitSftpServer.startSftpServer(sftpConfig, userFactoryList))
    }

    override fun stop() {

    }
}
