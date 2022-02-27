package io.github.zhangbinhub.acp.core.task.timer

import io.github.zhangbinhub.acp.core.exceptions.TimerException
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.task.BaseAsyncTask
import io.github.zhangbinhub.acp.core.task.timer.rule.CircleType
import io.github.zhangbinhub.acp.core.task.timer.rule.ExecuteType
import io.github.zhangbinhub.acp.core.tools.CommonUtils
import org.joda.time.DateTime

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class TimerTaskContainer(
    /**
     * 执行任务
     */
    private val task: BaseAsyncTask,
    /**
     * 执行周期
     */
    private val circleType: CircleType,
    /**
     * 执行时间点规则
     */
    private val rules: String,
    /**
     * 执行类型
     */
    private val executeType: ExecuteType
) : Runnable {

    /**
     * 日志对象
     */
    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 是否需要启动时立即执行
     */
    val needExecuteImmediate: Boolean

    /**
     * 上次执行时间
     */
    private var lastExecuteDateTime: DateTime

    init {
        lastExecuteDateTime = CommonUtils.getNowDateTime()
        needExecuteImmediate = task.needExecuteImmediate
    }

    /**
     * 获取容器内任务名称
     *
     * @return 任务名称
     */
    fun getTaskName(): String = task.taskName

    /**
     * 判断当前时间是否符合执行条件
     *
     * @return 结果对象
     */
    private fun isExecute(): Boolean {
        val now = CommonUtils.getNowDateTime()
        try {
            val flag: Boolean = when (executeType) {
                ExecuteType.WeekDay -> Calculation.isWeekDay(now)
                ExecuteType.Weekend -> Calculation.isWeekend(now)
                ExecuteType.All -> true
            }
            return when (circleType) {
                CircleType.Time -> true
                CircleType.Day -> {
                    val validate = Calculation.validateDay(now, lastExecuteDateTime, rules)
                    flag && validate
                }
                CircleType.Week -> Calculation.validateWeek(now, lastExecuteDateTime, rules)
                CircleType.Month -> Calculation.validateMonth(now, lastExecuteDateTime, rules)
                CircleType.Quarter -> Calculation.validateQuarter(now, lastExecuteDateTime, rules)
                CircleType.Year -> Calculation.validateYear(now, lastExecuteDateTime, rules)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            return false
        }

    }

    /**
     * 立即执行任务
     */
    fun immediateRun(): Any? {
        return task.doExecute()
    }

    override fun run() {
        try {
            if (isExecute()) {
                doExecute()
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    /**
     * 执行任务
     */
    @Throws(TimerException::class)
    private fun doExecute() {
        log.info(
            "begin TimerTask,taskName:["
                    + task.taskName
                    + "] className:["
                    + task.javaClass.canonicalName
                    + "] creatTime:"
                    + task.generateTime?.toString(Calculation.DATETIME_FORMAT)
                    + " submitTime:"
                    + task.submitTime?.toString(Calculation.DATETIME_FORMAT)
        )
        lastExecuteDateTime = CommonUtils.getNowDateTime()
        if (task.doExecute() == null) {
            log.error("timerTask [" + task.taskName + "] execute failed...")
        }
    }
}
