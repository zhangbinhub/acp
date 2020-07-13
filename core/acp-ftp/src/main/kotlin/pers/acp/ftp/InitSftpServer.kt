package pers.acp.ftp

import pers.acp.core.CommonTools
import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.ftp.base.InitServer
import pers.acp.ftp.conf.SftpConfig
import pers.acp.ftp.server.SftpServer
import pers.acp.ftp.user.UserFactory
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
object InitSftpServer : InitServer() {

    /**
     * 日志对象
     */
    private val log = LogFactory.getInstance(InitSftpServer::class.java)

    @JvmStatic
    @JvmOverloads
    fun startSftpServer(sftpConfig: SftpConfig? = null, userFactoryList: List<UserFactory>? = null): List<IDaemonService> {
        log.info("start sftp servers ...")
        userFactoryList?.forEach { userFactory -> addUserFactory(userFactory) }
        var sftpServers: List<IDaemonService> = ArrayList()
        try {
            sftpServers = doStart(sftpConfig ?: SftpConfig.getInstance())
        } catch (e: Exception) {
            log.error(e.message, e)
            log.info("start sftp servers exception: " + e.message)
        }
        return sftpServers
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    private fun doStart(sftpConfig: SftpConfig?): List<IDaemonService> {
        val sftpServers = ArrayList<IDaemonService>()
        sftpConfig?.let {
            val listens = it.listens
            if (listens != null && listens.isNotEmpty()) {
                for (listen in listens) {
                    if (listen.enabled) {
                        val classname = listen.userFactoryClass
                        if (!CommonTools.isNullStr(classname)) {
                            val userFactory = getUserFactory(classname!!)
                            val sftpServer = SftpServer(userFactory.generateSFtpUserList(), listen)
                            val sub = Thread(sftpServer)
                            sub.isDaemon = true
                            sub.start()
                            sftpServers.add(sftpServer)
                        } else {
                            log.info("start sftp server failed [" + listen.name + "] : user factory class is null")
                        }
                    } else {
                        log.info("sftp server is disabled [" + listen.name + "]")
                    }
                }
            } else {
                log.info("No sftp service was found")
            }
        }
        return sftpServers
    }
}