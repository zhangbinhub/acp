package com.github.zhangbinhub.acp.core.ftp.server

import com.github.zhangbinhub.acp.core.ftp.user.ServerUser

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
data class FtpServerUser(
        var writePermission: Boolean = false,
        var maxLoginNumber: Int = 0,
        var maxLoginPerIp: Int = 0,
        var idleTime: Int = 0,
        var uploadRate: Int = 0,
        var downloadRate: Int = 0
) : ServerUser()