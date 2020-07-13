package pers.acp.ftp

import pers.acp.core.CommonTools
import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.ftp.base.InitServer
import pers.acp.ftp.conf.FtpConfig
import pers.acp.ftp.server.FtpServer
import pers.acp.ftp.user.UserFactory
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
object InitFtpServer : InitServer() {

    /**
     * 日志对象
     */
    private val log = LogFactory.getInstance(InitFtpServer::class.java)

    @JvmStatic
    @JvmOverloads
    fun startFtpServer(ftpConfig: FtpConfig? = null, userFactoryList: List<UserFactory>? = null): List<IDaemonService> {
        log.info("start ftp servers ...")
        userFactoryList?.forEach { userFactory -> addUserFactory(userFactory) }
        var ftpServers: List<IDaemonService> = ArrayList()
        try {
            ftpServers = doStart(ftpConfig ?: FtpConfig.getInstance())
        } catch (e: Exception) {
            log.error(e.message, e)
            log.info("start ftp servers exception: " + e.message)
        }
        return ftpServers
    }

    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    private fun doStart(ftpConfig: FtpConfig?): List<IDaemonService> {
        val ftpServers = ArrayList<IDaemonService>()
        ftpConfig?.let {
            val listens = it.listens
            if (listens != null && listens.isNotEmpty()) {
                for (listen in listens) {
                    if (listen.enabled) {
                        val classname = listen.userFactoryClass
                        if (!CommonTools.isNullStr(classname)) {
                            val userFactory = getUserFactory(classname!!)
                            val ftpServer = FtpServer(userFactory.generateFtpUserList(), listen)
                            val sub = Thread(ftpServer)
                            sub.isDaemon = true
                            sub.start()
                            ftpServers.add(ftpServer)
                        } else {
                            log.info("start ftp server failed [" + listen.name + "] : user factory class is null")
                        }
                    } else {
                        log.info("ftp server is disabled [" + listen.name + "]")
                    }
                }
            } else {
                log.info("No ftp service was found")
            }
        }
        return ftpServers
    }
}