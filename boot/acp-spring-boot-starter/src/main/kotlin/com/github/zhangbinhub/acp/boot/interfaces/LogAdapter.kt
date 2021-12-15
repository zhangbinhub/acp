package com.github.zhangbinhub.acp.boot.interfaces

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
interface LogAdapter {
    fun info(message: String?)
    fun info(message: String?, vararg variable: Any?)
    fun info(message: String?, t: Throwable?)
    fun debug(message: String?)
    fun debug(message: String?, vararg variable: Any?)
    fun debug(message: String?, t: Throwable?)
    fun warn(message: String?)
    fun warn(message: String?, vararg variable: Any?)
    fun warn(message: String?, t: Throwable?)
    fun error(message: String?)
    fun error(message: String?, vararg variable: Any?)
    fun error(message: String?, t: Throwable?)
    fun trace(message: String?)
    fun trace(message: String?, vararg variable: Any?)
    fun trace(message: String?, t: Throwable?)
}