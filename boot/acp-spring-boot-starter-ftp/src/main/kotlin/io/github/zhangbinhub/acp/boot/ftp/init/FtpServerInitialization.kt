package io.github.zhangbinhub.acp.boot.ftp.init

import io.github.zhangbinhub.acp.boot.base.BaseInitialization
import io.github.zhangbinhub.acp.boot.daemon.DaemonServiceManager
import io.github.zhangbinhub.acp.boot.ftp.conf.FtpServerConfiguration
import io.github.zhangbinhub.acp.core.ftp.InitFtpServer
import io.github.zhangbinhub.acp.core.ftp.conf.FtpConfig
import io.github.zhangbinhub.acp.core.ftp.user.UserFactory

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class FtpServerInitialization(
    private val ftpServerConfiguration: FtpServerConfiguration,
    private val userFactoryList: List<UserFactory>?
) : BaseInitialization() {

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
