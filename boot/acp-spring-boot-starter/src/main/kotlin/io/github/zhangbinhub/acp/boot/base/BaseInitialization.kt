package io.github.zhangbinhub.acp.boot.base

/**
 * 系统初始化服务基类
 *
 * @author zhangbin by 2018-1-31 13:04
 * @since JDK 11
 */
abstract class BaseInitialization {

    abstract val name: String

    abstract val order: Int

    abstract fun start()

    abstract fun stop()

}
