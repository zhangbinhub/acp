package com.github.zhangbinhub.acp.boot.base

import com.github.zhangbinhub.acp.core.task.BaseAsyncTask

/**
 * 定时任务基类
 *
 * @author zhangbin by 2018-1-31 13:04
 * @since JDK 11
 */
/**
 * 构造函数
 */
abstract class BaseSpringBootScheduledAsyncTask : BaseAsyncTask("", false) {

    /**
     * 该任务是否处于等待状态
     *
     * @return true|false
     */
    val waiting: Boolean
        get() = !this.running

    /**
     * spring boot ScheduledTask 入口，注解需在此方法上
     */
    fun executeScheduledTask() {
        this.doExecute()
    }

}
