package com.github.zhangbinhub.acp.core.ftp.client

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.github.zhangbinhub.acp.core.CommonTools
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.ftp.exceptions.SftpException
import java.io.File
import java.util.*

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class SftpClient : BaseClient {

    private val log = LogFactory.getInstance(this.javaClass)

    private var channel: Channel? = null

    private var sftp: ChannelSftp? = null

    private var session: Session? = null

    /**
     * 密钥文件的路径
     */
    private var keyFilePath: String = ""

    /**
     * 密钥口令
     */
    private var passphrase: String = ""

    var serverCharset = "GBK"

    var channelMode = SftpChannelMode.RESUME

    /**
     * 构造基于用户密码的sftp对象
     *
     * @param hostname    远程主机地址
     * @param port        端口号
     * @param username    用户名
     * @param password    密码
     */
    constructor(hostname: String, port: Int, username: String, password: String) : super(hostname, port, username, password)

    /**
     * 构造基于秘钥认证的sftp对象
     *
     * @param keyFilePath 密钥文件绝对路径
     * @param passphrase  密钥口令
     * @param hostname    远程主机地址
     * @param port        端口号
     * @param username    用户名
     */
    constructor(keyFilePath: String, passphrase: String, hostname: String, port: Int, username: String) : super(hostname, port, username, "") {
        this.keyFilePath = keyFilePath
        this.passphrase = passphrase
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    private fun rebuildSftp() {
        sftp?.let {
            val field = it.javaClass.getDeclaredField("server_version")
            field.isAccessible = true
            field.set(sftp, 2)
        }
    }

    /**
     * 连接sftp服务器
     */
    @Throws(SftpException::class)
    private fun connect() {
        try {
            val jsCh = JSch()
            if (!CommonTools.isNullStr(keyFilePath)) {
                // 设置密钥
                if (CommonTools.isNullStr(passphrase)) {
                    jsCh.addIdentity(keyFilePath)
                } else {
                    jsCh.addIdentity(keyFilePath, passphrase)
                }
                log.info("sftp connect,path of private key file：{$keyFilePath}")
            }
            session = jsCh.getSession(username, hostname, port)
            if (!CommonTools.isNullStr(password)) {
                session!!.setPassword(password)
            }
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session!!.setConfig(config)
            session!!.setDaemonThread(true)
            session!!.connect()
            channel = session!!.openChannel("sftp")
            channel!!.connect()
            sftp = channel as ChannelSftp?
            rebuildSftp()
            sftp!!.setFilenameEncoding(serverCharset)
            log.info(String.format("sftp server hostname:[%s] port:[%s] is connect successFull", hostname, port))
        } catch (e: Exception) {
            log.error("Cannot connect to specified sftp server : {" + hostname + "}:{" + port + "} \n Exception message is: {" + e.message + "}")
            throw SftpException(e.message)
        }

    }

    /**
     * 递归创建目录
     *
     * @param remotePath 远程路径
     */
    @Throws(Exception::class)
    private fun createDirectory(remotePath: String) {
        try {
            if (!CommonTools.isNullStr(remotePath)) {
                sftp!!.cd(remotePath)
            }
        } catch (e: SftpException) {
            val tmpFold = parseCurrAndSubFold(remotePath)
            val fold = tmpFold[0]
            val subFold = tmpFold[1]
            if (!CommonTools.isNullStr(fold)) {
                try {
                    sftp!!.cd(fold)
                } catch (e1: SftpException) {
                    sftp!!.mkdir(fold)
                    sftp!!.cd(fold)
                }

            }
            createDirectory(subFold)
        }

    }

    /**
     * 关闭连接 server
     */
    private fun finallyFunc() {
        sftp?.let {
            if (it.isConnected) {
                it.disconnect()
            }
            sftp = null
        }
        channel?.let {
            if (it.isConnected) {
                it.disconnect()
            }
            channel = null
        }
        session?.let {
            if (it.isConnected) {
                it.disconnect()
            }
            session = null
        }
    }

    /**
     * 文件下载
     *
     * @return 本地文件绝对路径
     */
    fun doDownLoadForSFTP(): String {
        try {
            if (CommonTools.isNullStr(localPath)) {
                throw SftpException("localPath is null")
            }
            if (CommonTools.isNullStr(fileName)) {
                throw SftpException("fileName is null")
            }
            val path = localPath.replace("\\", File.separator).replace("/", File.separator)
            connect()
            formatRemotePath()
            val remoteFile = remotePath + fileName
            val localFile = path + File.separator + fileName + ".tmp"
            val localRealFile = path + File.separator + fileName
            val realFile = File(localRealFile)
            if (realFile.exists()) {
                log.info("sftp download successFull: $localRealFile")
                return localRealFile
            }
            val file = File(localFile)
            sftp!!.get(remoteFile, localFile, null, channelMode.value)
            return if (file.renameTo(realFile)) {
                log.info("sftp download successFull: $localRealFile")
                localRealFile
            } else {
                ""
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            log.error("sftp download failed!")
            return ""
        } finally {
            finallyFunc()
        }
    }

    /**
     * 上传文件
     */
    fun doUploadForSFTP(localFile: File?) {
        try {
            if (localFile == null) {
                throw SftpException("localFile is null")
            }
            if (CommonTools.isNullStr(fileName)) {
                throw SftpException("fileName is null")
            }
            connect()
            formatRemotePath()
            try {
                sftp!!.cd(remotePath)
            } catch (e: SftpException) {
                if (remotePath.startsWith("/")) {
                    remotePath = remotePath.substring(1)
                }
                createDirectory(remotePath)
            }
            sftp!!.put(localFile.canonicalPath, fileName, ChannelSftp.RESUME)
            log.info("file:{" + localFile.canonicalPath + "} is upload successful")
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            finallyFunc()
        }
    }

    /**
     * 删除目录
     */
    fun doDeleteDirForSFTP(): Boolean =
            try {
                if (CommonTools.isNullStr(fileName)) {
                    throw SftpException("fileName is null")
                }
                connect()
                formatRemotePath()
                sftp!!.cd(remotePath)
                sftp!!.rmdir(fileName)
                true
            } catch (e: Exception) {
                log.error(e.message, e)
                false
            } finally {
                finallyFunc()
            }

    /**
     * 删除文件
     */
    fun doDeleteFileForSFTP(): Boolean =
            try {
                if (CommonTools.isNullStr(fileName)) {
                    throw SftpException("fileName is null")
                }
                connect()
                formatRemotePath()
                sftp!!.cd(remotePath)
                sftp!!.rm(fileName)
                true
            } catch (e: Exception) {
                log.error(e.message, e)
                false
            } finally {
                finallyFunc()
            }

    fun getFileEntityListForSFTP(): List<ChannelSftp.LsEntry> =
            try {
                connect()
                formatRemotePath()
                sftp!!.ls(remotePath).map { item -> item as ChannelSftp.LsEntry }
                        .filter { entity -> !listOf(".", "..").contains(entity.filename) }
            } catch (e: Exception) {
                log.error(e.message, e)
                listOf()
            } finally {
                finallyFunc()
            }
}
