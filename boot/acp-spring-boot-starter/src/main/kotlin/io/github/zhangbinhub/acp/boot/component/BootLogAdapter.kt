package io.github.zhangbinhub.acp.boot.component

import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
class BootLogAdapter : LogAdapter {

    private val log = LogFactory.getInstance(this.javaClass, 4)

    override fun info(message: String?) {
        log.info(message)
    }

    override fun info(message: String?, vararg variable: Any?) {
        log.info(message, variable)
    }

    override fun info(message: String?, t: Throwable?) {
        log.info(message, t)
    }

    override fun debug(message: String?) {
        log.debug(message)
    }

    override fun debug(message: String?, vararg variable: Any?) {
        log.debug(message, variable)
    }

    override fun debug(message: String?, t: Throwable?) {
        log.debug(message, t)
    }

    override fun warn(message: String?) {
        log.warn(message)
    }

    override fun warn(message: String?, vararg variable: Any?) {
        log.warn(message, variable)
    }

    override fun warn(message: String?, t: Throwable?) {
        log.warn(message, t)
    }

    override fun error(message: String?) {
        log.error(message)
    }

    override fun error(message: String?, vararg variable: Any?) {
        log.error(message, variable)
    }

    override fun error(message: String?, t: Throwable?) {
        log.error(message, t)
    }

    override fun trace(message: String?) {
        log.trace(message)
    }

    override fun trace(message: String?, vararg variable: Any?) {
        log.trace(message, variable)
    }

    override fun trace(message: String?, t: Throwable?) {
        log.trace(message, t)
    }
}