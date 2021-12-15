package io.github.zhangbinhub.acp.core.interfaces

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
interface IDaemonService {

    /**
     * 获取守护任务名称
     *
     * @return 守护任务名称
     */
    fun getServiceName(): String

    /**
     * 停止任务
     */
    fun stopService()

}