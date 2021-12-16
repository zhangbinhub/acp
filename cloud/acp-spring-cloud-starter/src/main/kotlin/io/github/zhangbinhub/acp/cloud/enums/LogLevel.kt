package io.github.zhangbinhub.acp.cloud.enums

import io.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

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
                map[level.name.uppercase()] = level
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): LogLevel {
            return if (map.containsKey(name.uppercase())) {
                map.getValue(name.uppercase())
            } else {
                throw EnumValueUndefinedException(LogLevel::class.java, name)
            }
        }
    }

}
