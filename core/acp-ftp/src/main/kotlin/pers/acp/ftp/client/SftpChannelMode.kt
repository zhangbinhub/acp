package pers.acp.ftp.client

import com.jcraft.jsch.ChannelSftp
import pers.acp.core.exceptions.EnumValueUndefinedException

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
                map[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): SftpChannelMode {
            if (map.containsKey(name.toUpperCase())) {
                return map.getValue(name.toUpperCase())
            }
            throw EnumValueUndefinedException(SftpChannelMode::class.java, name)
        }
    }
}