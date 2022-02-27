package io.github.zhangbinhub.acp.core.ftp.client

import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.ftp.exceptions.FtpException
import io.github.zhangbinhub.acp.core.log.LogFactory
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.*
import java.net.InetAddress

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class FtpClient(hostname: String, port: Int, username: String, password: String) :
    BaseClient(hostname, port, username, password) {

    private val log = LogFactory.getInstance(this.javaClass)

    private val ftpClient = FTPClient()

    private var outputStream: OutputStream? = null

    private var inputStream: InputStream? = null

    private var raf: RandomAccessFile? = null

    var serverCharset = "ISO-8859-1"

    var connectMode = FtpConnectMode.ACTIVE_LOCAL

    var activeServerHost: String? = null

    var activeServerPort: Int = 0

    /**
     * 连接到FTP服务器
     *
     * @return true|false
     */
    @Throws(Exception::class)
    private fun connect(): Boolean {
        try {
            ftpClient.connect(hostname, port)
            val reply = ftpClient.replyCode
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect()
                log.error("ftp connect error,hostname=$hostname port:$port")
                return false
            }
            if (!ftpClient.login(username, password)) {
                ftpClient.disconnect()
                log.error("ftp login error,username=$username password=$password")
                return false
            }
            //设置以二进制方式传输
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)
            when (connectMode) {
                FtpConnectMode.ACTIVE_LOCAL -> ftpClient.enterLocalActiveMode()
                FtpConnectMode.ACTIVE_REMOTE -> ftpClient.enterRemoteActiveMode(
                    InetAddress.getByName(activeServerHost),
                    activeServerPort
                )
                FtpConnectMode.PASSIVE_LOCAL -> ftpClient.enterLocalPassiveMode()
                FtpConnectMode.PASSIVE_REMOTE -> ftpClient.enterRemotePassiveMode()
            }
            log.info("ftp server by hostname:{$hostname} username:{$username} is connect successFull")
            return true
        } catch (e: Exception) {
            throw Exception("ftp connect failed!")
        }
    }

    /**
     * 递归创建目录
     *
     * @param remotePath 远程路径
     */
    @Throws(Exception::class)
    private fun createDirectory(remotePath: String) {
        if (remotePath != "/" && !ftpClient.changeWorkingDirectory(remotePath)) {
            val tmpFold = parseCurrAndSubFold(remotePath)
            val fold = tmpFold[0]
            val subFold = tmpFold[1]
            if (!CommonTools.isNullStr(fold)) {
                if (!ftpClient.changeWorkingDirectory(fold)) {
                    if (ftpClient.makeDirectory(fold)) {
                        ftpClient.changeWorkingDirectory(fold)
                    } else {
                        throw Exception("create remote fold is failed!")
                    }
                }
            }
            createDirectory(subFold)
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件路径
     * @param localFile  本地文件
     * @return true|false
     */
    @Throws(Exception::class)
    private fun uploadFile(remoteFile: String, localFile: File): Boolean {
        var remoteSize: Long
        //设置被动模式
        ftpClient.enterLocalPassiveMode()
        //检查远程是否存在文件
        val files = ftpClient.listFiles(remoteFile)
        if (files.size == 1) {
            remoteSize = files[0].size
            val localSize = localFile.length()
            if (remoteSize >= localSize) {
                if (!ftpClient.deleteFile(remoteFile)) {
                    throw Exception("delete remote file is failed!")
                }
                remoteSize = 0
            }
        } else {
            remoteSize = 0
        }
        raf = RandomAccessFile(localFile, "r")
        outputStream = ftpClient.appendFileStream(remoteFile)
        if (remoteSize > 0) {
            ftpClient.restartOffset = remoteSize
            raf!!.seek(remoteSize)
        }
        val bytes = ByteArray(1024)
        var c = raf!!.read(bytes)
        while (c != -1) {
            outputStream!!.write(bytes, 0, c)
            c = raf!!.read(bytes)
        }
        outputStream!!.flush()
        raf!!.close()
        outputStream!!.close()
        return ftpClient.completePendingCommand()
    }

    /**
     * 关闭连接 server
     */
    private fun finallyFunc() {
        try {
            inputStream?.let {
                it.close()
                inputStream = null
            }
            outputStream?.let {
                it.close()
                outputStream = null
            }
            raf?.let {
                it.close()
                raf = null
            }
            if (ftpClient.isConnected) {
                ftpClient.disconnect()
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    /**
     * 从FTP下载文件
     *
     * @return 本地文件绝对路径
     */
    fun doDownLoadForFTP(): String {
        try {
            if (CommonTools.isNullStr(localPath)) {
                throw FtpException("localPath is null")
            }
            if (CommonTools.isNullStr(fileName)) {
                throw FtpException("fileName is null")
            }
            val path = localPath.replace("\\", File.separator).replace("/", File.separator)
            if (!connect()) {
                throw FtpException("ftp server login failed!")
            }
            formatRemotePath()
            val remoteFile = String((remotePath + fileName).toByteArray(charset(charset)), charset(serverCharset))
            val localFile = path + File.separator + fileName + ".tmp"
            val localRealFile = path + File.separator + fileName
            //设置被动模式
            ftpClient.enterLocalPassiveMode()
            //检查远程文件是否存在
            val files = ftpClient.listFiles(remoteFile)
            if (files.size != 1) {
                throw FtpException("remote file is not find!")
            }
            val lRemoteSize = files[0].size
            val realFile = File(localRealFile)
            if (realFile.exists()) {
                log.info("ftp download successFull: $localRealFile")
                return localRealFile
            }
            val file = File(localFile)
            //本地存在文件，进行断点续传
            if (file.exists()) {
                val localSize = file.length()
                if (localSize == lRemoteSize) {
                    return if (file.renameTo(realFile)) {
                        log.info("ftp download successFull: $localRealFile")
                        localRealFile
                    } else {
                        ""
                    }
                }
                //进行断点续传，并记录状态
                outputStream = FileOutputStream(file, true)
                ftpClient.restartOffset = localSize
            } else {
                outputStream = FileOutputStream(file)
            }
            //设置被动模式
            ftpClient.enterLocalPassiveMode()
            inputStream = ftpClient.retrieveFileStream(remoteFile)
            val bytes = ByteArray(1024)
            var c = inputStream!!.read(bytes)
            while (c != -1) {
                outputStream!!.write(bytes, 0, c)
                c = inputStream!!.read(bytes)
            }
            inputStream!!.close()
            outputStream!!.close()
            val isDo = ftpClient.completePendingCommand()
            ftpClient.logout()
            ftpClient.disconnect()
            return if (isDo) {
                if (file.renameTo(realFile)) {
                    log.info("ftp download successFull: $localRealFile")
                    localRealFile
                } else {
                    ""
                }
            } else {
                log.error("ftp download failed!")
                ""
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            log.error("ftp download failed!")
            return ""
        } finally {
            finallyFunc()
        }
    }

    /**
     * 上传文件
     *
     * @param localFile 本地文件
     * @return 成功或失败
     */
    fun doUpLoadForFTP(localFile: File?): Boolean =
        try {
            if (localFile == null) {
                throw FtpException("localFile is null")
            }
            if (CommonTools.isNullStr(fileName)) {
                throw FtpException("fileName is null")
            }
            if (!connect()) {
                throw FtpException("ftp server login failed!")
            }
            formatRemotePath()
            ftpClient.controlEncoding = charset
            remotePath = String(remotePath.toByteArray(charset(charset)), charset(serverCharset))
            fileName = String(fileName.toByteArray(charset(charset)), charset(serverCharset))
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                if (remotePath.startsWith("/")) {
                    remotePath = remotePath.substring(1)
                }
                createDirectory(remotePath)
            }
            val uploadResult = uploadFile(fileName, localFile)
            ftpClient.logout()
            ftpClient.disconnect()
            if (uploadResult) {
                log.info("ftp download successFull{" + localFile.name + "}: " + localFile.canonicalPath)
            }
            uploadResult
        } catch (e: Exception) {
            log.error(e.message, e)
            false
        } finally {
            finallyFunc()
        }

    /**
     * 删除文件
     *
     * @return 成功或失败
     */
    fun doDeleteForFTP(): Boolean =
        try {
            if (CommonTools.isNullStr(fileName)) {
                throw FtpException("fileName is null")
            }
            if (!connect()) {
                throw FtpException("ftp server login failed!")
            }
            formatRemotePath()
            remotePath = String(remotePath.toByteArray(charset(charset)), charset(serverCharset))
            fileName = String(fileName.toByteArray(charset(charset)), charset(serverCharset))
            ftpClient.changeWorkingDirectory(remotePath)
            var result = true
            val files = ftpClient.listFiles(fileName)
            if (files.size == 1) {
                result = ftpClient.deleteFile(fileName)
            }
            ftpClient.logout()
            ftpClient.disconnect()
            result
        } catch (e: Exception) {
            log.error(e.message, e)
            false
        } finally {
            finallyFunc()
        }

    fun getFileEntityListForFTP(): List<FTPFile> =
        try {
            if (!connect()) {
                throw FtpException("ftp server login failed!")
            }
            formatRemotePath()
            remotePath = String(remotePath.toByteArray(charset(charset)), charset(serverCharset))
            ftpClient.changeWorkingDirectory(remotePath)
            ftpClient.listFiles()
            val fileList = ftpClient.listFiles(remotePath).toList()
            ftpClient.logout()
            ftpClient.disconnect()
            fileList
        } catch (e: Exception) {
            log.error(e.message, e)
            listOf()
        } finally {
            finallyFunc()
        }
}
