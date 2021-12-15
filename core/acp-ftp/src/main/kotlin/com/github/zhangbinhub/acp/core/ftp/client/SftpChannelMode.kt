package com.github.zhangbinhub.acp.core.ftp.client

import com.jcraft.jsch.ChannelSftp
import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
enum class SftpChannelMode(val value: Int) {
    OVERWRITE(ChannelSftp.OVERWRITE),
    RESUME(ChannelSftp.RESUME),
    APPEND(ChannelSftp.APPEND);

    companion object {

        private var map: MutableMap<String, SftpChannelMode> = mutableMapOf()

        init {
            for (type in values()) {
                map[type.name.uppercase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): SftpChannelMode {
            if (map.containsKey(name.uppercase())) {
                return map.getValue(name.uppercase())
            }
            throw EnumValueUndefinedException(SftpChannelMode::class.java, name)
        }
    }
}