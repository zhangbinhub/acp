package pers.acp.ftp.server

import pers.acp.ftp.user.ServerUser

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
data class SftpServerUser(var publicKey: String? = null) : ServerUser()