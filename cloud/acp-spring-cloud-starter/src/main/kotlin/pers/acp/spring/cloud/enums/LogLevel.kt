package pers.acp.spring.cloud.enums

import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * 日志记录级别
 *
 * @author zhangbin by 11/07/2018 14:54
 * @since JDK 11
 */
enum class LogLevel {
    Info,
    Debug,
    Warn,
    Error,
    Trace,
    Other;

    companion object {
        private var map: MutableMap<String, LogLevel> = mutableMapOf()

        init {
            for (level in values()) {
                map[level.name.toUpperCase()] = level
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): LogLevel {
            return if (map.containsKey(name.toUpperCase())) {
                map.getValue(name.toUpperCase())
            } else {
                throw EnumValueUndefinedException(LogLevel::class.java, name)
            }
        }
    }

}
