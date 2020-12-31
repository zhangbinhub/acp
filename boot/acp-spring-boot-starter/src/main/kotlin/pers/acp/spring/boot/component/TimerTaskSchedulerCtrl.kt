package pers.acp.spring.boot.component

import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask
import pers.acp.spring.boot.conf.ScheduleConfiguration
import pers.acp.spring.boot.interfaces.TimerTaskScheduler
import pers.acp.core.CommonTools
import pers.acp.spring.boot.interfaces.LogAdapter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

/**
 * 定时任务处理器
 *
 * @author zhangbin by 2018-1-20 21:24
 * @since JDK 11
 */
class TimerTaskSchedulerCtrl(private val logAdapter: LogAdapter,
                             properties: TaskSchedulingProperties,
                             private val scheduleConfiguration: ScheduleConfiguration,
                             private val baseSpringBootScheduledTaskMap: Map<String, BaseSpringBootScheduledAsyncTask>) : TimerTaskScheduler {

    private val threadPoolTaskScheduler: ThreadPoolTaskScheduler = ThreadPoolTaskScheduler()

    private val scheduledTaskMap = ConcurrentHashMap<String, BaseSpringBootScheduledAsyncTask>()

    private val futureMap = ConcurrentHashMap<String, ScheduledFuture<*>>()

    init {
        this.threadPoolTaskScheduler.poolSize = properties.pool.size
        this.threadPoolTaskScheduler.threadNamePrefix = properties.threadNamePrefix
        this.threadPoolTaskScheduler.initialize()
    }

    /**
     * 启动定时任务
     */
    @Throws(InterruptedException::class)
    private fun startSchedule() {
        if (!scheduledTaskMap.isEmpty() || !futureMap.isEmpty()) {
            stopSchedule()
        }
        baseSpringBootScheduledTaskMap.forEach { (key, scheduledTask) ->
            val cronMap = scheduleConfiguration.cron
            if (cronMap.isNotEmpty() && cronMap.containsKey(key) && !CommonTools.isNullStr(cronMap[key]) && !"none".equals(cronMap[key], ignoreCase = true)) {
                cronMap[key]?.apply {
                    scheduledTaskMap[key] = scheduledTask
                    threadPoolTaskScheduler.schedule({ scheduledTask.executeScheduledTask() }, CronTrigger(this))?.let {
                        futureMap[key] = it
                        logAdapter.info("启动定时任务：" + scheduledTask.taskName + " 【" + this + "】 【" + scheduledTask.javaClass.canonicalName + "】")
                    }
                }
            }
        }
    }

    /**
     * 停止定时任务
     */
    @Throws(InterruptedException::class)
    private fun stopSchedule() {
        val iterator = scheduledTaskMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val key = entry.key
            val scheduledTask = entry.value
            val future = futureMap.remove(key)
            while (!scheduledTask.waiting) {
                Thread.sleep(3000)
            }
            future?.cancel(true)
            logAdapter.info("停止定时任务：" + scheduledTask.taskName + " 【" + scheduledTask.javaClass.canonicalName + "】")
            iterator.remove()
        }
    }

    /**
     * 定时任务控制
     *
     * @param command ITimerTaskScheduler.START | ITimerTaskScheduler.STOP
     * @throws InterruptedException 异常
     */
    @Throws(InterruptedException::class)
    override fun controlSchedule(command: Int) {
        synchronized(this) {
            if (command == TimerTaskScheduler.START) {
                startSchedule()
            } else if (command == TimerTaskScheduler.STOP) {
                stopSchedule()
            }
        }
    }

}
