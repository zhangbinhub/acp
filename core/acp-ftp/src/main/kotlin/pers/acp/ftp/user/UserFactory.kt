package pers.acp.ftp.user

import pers.acp.ftp.server.FtpServerUser
import pers.acp.ftp.server.SftpServerUser

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
interface UserFactory {

    fun generateFtpUserList(): List<FtpServerUser>

    fun generateSFtpUserList(): List<SftpServerUser>
}