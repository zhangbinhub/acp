package io.github.zhangbinhub.acp.boot.interfaces

/**
 * Create by zhangbin on 2017-10-28 0:56
 */
interface TimerTaskScheduler {

    /**
     * 定时器控制
     *
     * @param command ITimerTaskScheduler.START | ITimerTaskScheduler.STOP
     */
    @Throws(InterruptedException::class)
    fun controlSchedule(command: Int)

    companion object {

        const val START = 1

        const val STOP = 0
    }

}
