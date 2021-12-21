package io.github.zhangbinhub.acp.boot.component

import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
class BootLogAdapter : LogAdapter {
    private val stackIndex = 4
    private fun logInstance(): LogFactory =
        LogFactory.getInstance(Thread.currentThread().stackTrace[stackIndex - 1].className, stackIndex)

    override fun info(message: String?) {
        logInstance().info(message)
    }

    override fun info(message: String?, vararg variable: Any?) {
        logInstance().info(message, variable)
    }

    override fun info(message: String?, t: Throwable?) {
        logInstance().info(message, t)
    }

    override fun debug(message: String?) {
        logInstance().debug(message)
    }

    override fun debug(message: String?, vararg variable: Any?) {
        logInstance().debug(message, variable)
    }

    override fun debug(message: String?, t: Throwable?) {
        logInstance().debug(message, t)
    }

    override fun warn(message: String?) {
        logInstance().warn(message)
    }

    override fun warn(message: String?, vararg variable: Any?) {
        logInstance().warn(message, variable)
    }

    override fun warn(message: String?, t: Throwable?) {
        logInstance().warn(message, t)
    }

    override fun error(message: String?) {
        logInstance().error(message)
    }

    override fun error(message: String?, vararg variable: Any?) {
        logInstance().error(message, variable)
    }

    override fun error(message: String?, t: Throwable?) {
        logInstance().error(message, t)
    }

    override fun trace(message: String?) {
        logInstance().trace(message)
    }

    override fun trace(message: String?, vararg variable: Any?) {
        logInstance().trace(message, variable)
    }

    override fun trace(message: String?, t: Throwable?) {
        logInstance().trace(message, t)
    }
}