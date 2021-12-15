package com.github.zhangbinhub.acp.boot.ftp.init

import com.github.zhangbinhub.acp.core.ftp.InitSftpServer
import com.github.zhangbinhub.acp.core.ftp.conf.SftpConfig
import com.github.zhangbinhub.acp.core.ftp.user.UserFactory
import com.github.zhangbinhub.acp.boot.base.BaseInitialization
import com.github.zhangbinhub.acp.boot.daemon.DaemonServiceManager
import com.github.zhangbinhub.acp.boot.ftp.conf.SftpServerConfiguration

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
