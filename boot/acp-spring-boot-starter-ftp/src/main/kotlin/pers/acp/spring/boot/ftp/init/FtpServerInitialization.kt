package pers.acp.spring.boot.ftp.init

import pers.acp.ftp.InitFtpServer
import pers.acp.ftp.conf.FtpConfig
import pers.acp.ftp.user.UserFactory
import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.spring.boot.daemon.DaemonServiceManager
import pers.acp.spring.boot.ftp.conf.FtpServerConfiguration

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class FtpServerInitialization(private val ftpServerConfiguration: FtpServerConfiguration,
                              private val userFactoryList: List<UserFactory>?) : BaseInitialization() {

    override val name: String
        get() = "ftp server setup server"

    override val order: Int
        get() = 1

    /**
     * 启动ftp服务
     */
    override fun start() {
        val ftpConfig = FtpConfig()
        ftpConfig.listens = ftpServerConfiguration.listeners
        DaemonServiceManager.addAllService(InitFtpServer.startFtpServer(ftpConfig, userFactoryList))
    }

    override fun stop() {

    }
}
