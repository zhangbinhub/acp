package pers.acp.core.task.timer

import pers.acp.core.log.LogFactory
import pers.acp.core.task.BaseAsyncTask
import pers.acp.core.task.timer.rule.CircleType
import pers.acp.core.task.timer.rule.ExecuteType
import pers.acp.core.tools.CommonUtils
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class TimerDriver
/**
 * 构造函数
 *
 * @param threadNumber 工作线程数
 * @param circleType   执行周期:Time, Day, Week, Month, Quarter, Year
 * @param rules        执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）,
 * 日-时间（HH:MI:SS）, 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
 * 季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
 * @param executeType  执行类型:WeekDay, Weekend, All
 */
(threadNumber: Int, private val circleType: CircleType = CircleType.Day, private val rules: String = "00:00:00", private val executeType: ExecuteType = ExecuteType.All) : ScheduledThreadPoolExecutor(threadNumber) {

    private val log = LogFactory.getInstance(javaClass)// 日志对象

    /**
     * 任务容器
     */
    private var container: TimerTaskContainer? = null

    /**
     * 获取定时器信息
     */
    fun timerInfo(): String {
        var info = "\ncircleType:" + circleType.name + "\nrules:" + rules + "\nexecuteType:" + executeType.name
        container?.let {
            info += "\ntimerTask[" + it.getTaskName() + "]\nneedExecuteImmediate=" + it.needExecuteImmediate
        }
        return info
    }

    /**
     * 设置定时任务
     *
     * @param task 任务对象
     */
    private fun setTimerTask(task: BaseAsyncTask): Boolean {
        if (container != null) {
            log.error("a TimerDriver can only receive one task")
            return false
        }
        task.submitTime = CommonUtils.getNowDateTime()
        container = TimerTaskContainer(task, circleType, rules, executeType)
        return true
    }

    /**
     * 启动定时器
     */
    fun startTimer(task: BaseAsyncTask) {
        if (setTimerTask(task)) {
            runTimerTask()
        }
    }

    /**
     * 停止定时器
     */
    fun stopTimer() {
        shutdown()
        log.info("stop timerDriver!")
    }

    /**
     * 启动定时器,执行定时任务
     */
    private fun runTimerTask() = try {
        container?.let {
            if (it.needExecuteImmediate) {
                it.immediateRun()
            }
            val param = Calculation.getTimerParam(circleType, rules)
            scheduleAtFixedRate(it, param[0], param[1], TimeUnit.MILLISECONDS)
            log.info("start timerTask successFull:" + timerInfo())
        }
    } catch (e: Exception) {
        log.error(e.message, e)
        stopTimer()
    }

}
