package pers.acp.ftp.client

import org.apache.commons.net.ftp.FTPClient
import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
enum class FtpConnectMode(val value: Int) {
    ACTIVE_LOCAL(FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE),
    ACTIVE_REMOTE(FTPClient.ACTIVE_REMOTE_DATA_CONNECTION_MODE),
    PASSIVE_LOCAL(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE),
    PASSIVE_REMOTE(FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE);

    companion object {

        private var map: MutableMap<String, FtpConnectMode> = mutableMapOf()

        init {
            for (type in values()) {
                map[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): FtpConnectMode {
            if (map.containsKey(name.toUpperCase())) {
                return map.getValue(name.toUpperCase())
            }
            throw EnumValueUndefinedException(FtpConnectMode::class.java, name)
        }
    }
}