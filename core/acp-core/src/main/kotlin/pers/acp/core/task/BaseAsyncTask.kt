package pers.acp.core.task

import org.joda.time.DateTime
import pers.acp.core.log.LogFactory
import pers.acp.core.tools.CommonUtils

import java.util.concurrent.Callable
import java.util.concurrent.CancellationException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
abstract class BaseAsyncTask(var taskName: String, var needExecuteImmediate: Boolean = false) : IBaseTask, Callable<Any> {

    /**
     * 日志对象
     */
    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 任务ID
     */
    var taskId: String? = null
        private set

    /**
     * 创建时间
     */
    var generateTime: DateTime? = null
        private set

    /**
     * 提交执行时间
     */
    var submitTime: DateTime? = null

    /**
     * 开始执行时间
     */
    var beginExecuteTime: DateTime? = null
        private set

    /**
     * 执行完成时间
     */
    var finishTime: DateTime? = null
        private set

    /**
     * 任务执行结果
     */
    var taskResult: Any? = null
        protected set

    /**
     * 是否需要立即执行：false-等待执行策略；true-立即执行
     */

    /**
     * 任务是否处于正在执行状态
     */
    @Volatile
    var running = false
        private set

    init {
        this.taskId = CommonUtils.getUuid()
        this.generateTime = CommonUtils.getNowDateTime()
    }

    override fun call(): Any? {
        this.running = true
        this.beginExecuteTime = DateTime()
        var result: Any? = null
        try {
            if (this.beforeExecuteFun()) {
                result = this.executeFun()
                this.taskResult = result
                result?.let {
                    this.afterExecuteFun(it)
                }
            }
        } catch (interrupt: InterruptedException) {
            log.error("task[${taskName}] thread is interrupted : " + interrupt.message)
            result = null
        } catch (cancel: CancellationException) {
            log.error("task[${taskName}] is canceled : " + cancel.message)
            result = null
        } catch (e: Exception) {
            log.error(e.message, e)
            result = null
        }

        this.finishTime = CommonUtils.getNowDateTime()
        this.running = false
        this.taskResult = result
        return result
    }

    /**
     * 执行任务，返回执行结果
     *
     * @return 执行结果对象
     */
    fun doExecute(): Any? {
        return call()
    }

}
