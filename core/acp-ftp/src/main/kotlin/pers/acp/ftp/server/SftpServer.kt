package pers.acp.ftp.server

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import pers.acp.core.CommonTools
import pers.acp.core.interfaces.IDaemonService
import pers.acp.core.log.LogFactory
import pers.acp.ftp.conf.SftpListener
import pers.acp.ftp.exceptions.SftpServerException
import java.nio.file.FileSystems

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class SftpServer(private val userList: List<SftpServerUser>, private val listen: SftpListener) : Runnable, IDaemonService {

    private val log = LogFactory.getInstance(this.javaClass)

    private var sshServer: SshServer? = null

    override fun getServiceName(): String {
        return "sftp service " + listen.name
    }

    override fun stopService() {
        try {
            sshServer?.stop(true)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    override fun run() {
        try {
            if (CommonTools.isNullStr(listen.defaultHomeDirectory)) {
                throw SftpServerException("defaultHomeDirectory is null")
            }
            val defaultHomeDirectory = CommonTools.getAbsPath(listen.defaultHomeDirectory!!)
            if (userList.isEmpty()) {
                log.error("start sftp server failed [" + listen.name + "] : no user set!")
                throw SftpServerException("no user set")
            }

            val keyAuthType = listen.keyAuthType
            if (listen.publicKeyAuth && CommonTools.isNullStr(keyAuthType)) {
                log.error("start sftp server failed [" + listen.name + "] : keyAuthType is not support [" + keyAuthType + "] !")
                throw SftpServerException("keyAuthType is not support : $keyAuthType")
            } else {
                if (keyAuthType != "der" && keyAuthType != "pem" && keyAuthType != "ssh") {
                    log.error("start sftp server failed [" + listen.name + "] : keyAuthType is not support [" + keyAuthType + "] !")
                    throw SftpServerException("keyAuthType is not support : $keyAuthType")
                }
            }

            val keyAuthMode = listen.keyAuthMode
            if (CommonTools.isNullStr(keyAuthMode)) {
                log.error("start sftp server failed [" + listen.name + "] : keyAuthMode is not support [" + keyAuthMode + "] !")
                throw SftpServerException("keyAuthMode is not support : $keyAuthMode")
            } else {
                if (keyAuthMode != "DSA" && keyAuthMode != "RSA") {
                    log.error("start sftp server failed [" + listen.name + "] : keyAuthMode is not support [" + keyAuthMode + "] !")
                    throw SftpServerException("keyAuthMode is not support : $keyAuthMode")
                }
            }

            sshServer = SshServer.setUpDefaultServer()
            sshServer!!.port = listen.port
            val keyPath = CommonTools.getAbsPath(listen.hostKeyPath!!)
            if (listen.publicKeyAuth) {
                sshServer!!.properties[SshServer.AUTH_METHODS] = "publickey"
                sshServer!!.publickeyAuthenticator = UserPublicKeyAuthenticator(userList, true, keyAuthMode, keyAuthType)
            } else {
                sshServer!!.publickeyAuthenticator = UserPublicKeyAuthenticator(userList, false, keyAuthMode, keyAuthType)
            }
            sshServer!!.keyPairProvider = SimpleGeneratorHostKeyProvider(FileSystems.getDefault().getPath(keyPath))

            val namedFactoryList = mutableListOf<SftpSubsystemFactory>()
            namedFactoryList.add(SftpSubsystemFactory())
            sshServer!!.subsystemFactories = namedFactoryList.toList()
            if (!listen.publicKeyAuth && listen.passwordAuth) {
                sshServer!!.passwordAuthenticator = UserPasswordAuthenticator(userList, true)
            } else {
                sshServer!!.passwordAuthenticator = UserPasswordAuthenticator(userList, false)
            }
            val virtualFileSystemFactory = VirtualFileSystemFactory(FileSystems.getDefault().getPath(defaultHomeDirectory))
            for (sftpServerUser in userList) {
                var homeDirectory = sftpServerUser.homeDirectory
                if (CommonTools.isNullStr(homeDirectory)) {
                    virtualFileSystemFactory.setUserHomeDir(sftpServerUser.username, FileSystems.getDefault().getPath(defaultHomeDirectory))
                } else {
                    homeDirectory = homeDirectory.replace("\\", "/")
                    if (!homeDirectory.startsWith("/")) {
                        homeDirectory = "/$homeDirectory"
                    }
                    if (defaultHomeDirectory == "/") {
                        virtualFileSystemFactory.setUserHomeDir(sftpServerUser.username, FileSystems.getDefault().getPath(homeDirectory))
                    } else {
                        virtualFileSystemFactory.setUserHomeDir(sftpServerUser.username, FileSystems.getDefault().getPath(defaultHomeDirectory + homeDirectory))
                    }
                }
            }
            sshServer!!.fileSystemFactory = virtualFileSystemFactory
            sshServer!!.start()
            log.info("sftp server [" + listen.name + "] is started, port : " + listen.port + ", path : " + defaultHomeDirectory)
        } catch (e: Exception) {
            log.error(e.message, e)
            log.error("start sftp server failed [" + listen.name + "] port:" + listen.port)
        }
    }
}