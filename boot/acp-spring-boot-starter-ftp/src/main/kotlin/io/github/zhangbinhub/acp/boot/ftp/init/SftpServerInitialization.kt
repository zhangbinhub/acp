package io.github.zhangbinhub.acp.boot.ftp.init

import io.github.zhangbinhub.acp.boot.base.BaseInitialization
import io.github.zhangbinhub.acp.boot.daemon.DaemonServiceManager
import io.github.zhangbinhub.acp.boot.ftp.conf.SftpServerConfiguration
import io.github.zhangbinhub.acp.core.ftp.InitSftpServer
import io.github.zhangbinhub.acp.core.ftp.conf.SftpConfig
import io.github.zhangbinhub.acp.core.ftp.user.UserFactory

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class SftpServerInitialization(
    private val sftpServerConfiguration: SftpServerConfiguration,
    private val userFactoryList: List<UserFactory>?
) : BaseInitialization() {

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
