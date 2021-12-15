package com.github.zhangbinhub.acp.core.ftp.server

import com.github.zhangbinhub.acp.core.ftp.user.ServerUser

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
data class SftpServerUser(var publicKey: String? = null) : ServerUser()