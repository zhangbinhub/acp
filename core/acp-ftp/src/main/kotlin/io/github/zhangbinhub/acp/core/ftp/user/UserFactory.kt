package io.github.zhangbinhub.acp.core.ftp.user

import io.github.zhangbinhub.acp.core.ftp.server.FtpServerUser
import io.github.zhangbinhub.acp.core.ftp.server.SftpServerUser

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
interface UserFactory {

    fun generateFtpUserList(): List<FtpServerUser>

    fun generateSFtpUserList(): List<SftpServerUser>
}