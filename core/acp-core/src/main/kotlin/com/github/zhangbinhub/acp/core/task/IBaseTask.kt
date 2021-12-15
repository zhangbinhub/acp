package com.github.zhangbinhub.acp.core.task

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
interface IBaseTask {

    /**
     * 任务执行前判断函数
     *
     * @return true-任务开始执行，false-任务拒绝执行
     */
    fun beforeExecuteFun(): Boolean

    /**
     * 执行任务函数
     *
     * @return Object-执行成功并进入afterExcute函数，null-执行失败不进入afterExcute函数
     */
    fun executeFun(): Any?

    /**
     * 任务执行后函数
     */
    fun afterExecuteFun(result: Any)

}