package com.github.zhangbinhub.acp.core.task.threadpool

import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.task.BaseAsyncTask
import com.github.zhangbinhub.acp.core.tools.CommonUtils

import java.util.concurrent.*

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class ThreadPoolService

/**
 * 线程池实例构造函数
 *
 * @param poolName        线程池实例名
 * @param maxFreeTime     线程最大空闲时间
 * @param minThreadNumber 最小线程数
 * @param maxThreadNumber 最大线程数
 * Int.MAX_VALUE--无界线程数，直接提交
 * @param querySize       队列容量；
 * Int.MAX_VALUE--无界队列，LinkedBlockingQueue
 * else---------------有界队列，ArrayBlockingQueue
 */
private constructor(private val poolName: String, maxFreeTime: Long, minThreadNumber: Int, maxThreadNumber: Int, querySize: Int) {

    private var executor: ThreadPoolExecutor

    init {
        when {
            maxThreadNumber == Int.MAX_VALUE ->
                /*
                 * 直接提交队列: SynchronousQueue
                 * 1、要求无界 maximumPoolSizes 以避免拒绝新提交的任务。其中每个插入操作必须等待另一个线程的对应移除操作，也就是说A任务进入队列，B任务必须等A任务被移除之后才能进入队列，否则执行异常策略
                 * 2、运行线程数 <  corePoolSize 时直接创建线程运行
                 * 3、运行线程数 >= corePoolSize 任务放入队列
                 * 4、队列中的任务处理逻辑：
                 *    运行线程数 < maximumPoolSizes 时创建新线程运行待加入队列的任务
                 *    运行线程数 = maximumPoolSizes 时执行异常策略
                 * 5、当一个线程空闲时间超过 maxFreeTime 且运行线程数 > corePoolSize 时，销毁空闲线程，直至运行线程数 = corePoolSize
                 */
                executor = ThreadPoolExecutor(0, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, SynchronousQueue())
            querySize == Int.MAX_VALUE ->
                /*
                 * 无界队列: LinkedBlockingQueue
                 * 1、将导致在所有核心线程都在忙时新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize。(因此，maximumPoolSize 的值也就没意义了。)
                 * 2、运行线程数 <  corePoolSize 时直接创建线程运行
                 * 3、运行线程数 >= corePoolSize 任务放入队列，
                 * 4、队列中的任务处理逻辑：
                 *    在队列中一直排队等待，直至有空闲线程继续处理或内存溢出
                 */
                executor = ThreadPoolExecutor(maxThreadNumber, maxThreadNumber, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
            else ->
                /*
                 * 有界队列: ArrayBlockingQueue
                 * 1、正常线程池，需适当调整 corePoolSize、maximumPoolSizes、querySize 的参数
                 * 2、运行线程数 <  corePoolSize 时直接创建线程运行
                 * 3、运行线程数 >= corePoolSize 任务放入队列
                 * 4、队列中的任务处理逻辑：
                 *    运行线程数 < maximumPoolSizes 且队列未满时（新任务加入队列前），新任务加入队列中一直排队等待
                 *    运行线程数 < maximumPoolSizes 且队列已满时（新任务加入队列前），创建新线程运行待加入队列的任务
                 *    运行线程数 = maximumPoolSizes 且队列未满时（新任务加入队列前），新任务加入队列中一直排队等待
                 *    运行线程数 = maximumPoolSizes 且队列已满时（新任务加入队列前），执行异常策略
                 * 5、当一个线程空闲时间超过 maxFreeTime 且运行线程数 > corePoolSize 时，销毁空闲线程，直至运行线程数 = corePoolSize
                 */
                executor = ThreadPoolExecutor(minThreadNumber, maxThreadNumber, maxFreeTime, TimeUnit.MILLISECONDS, ArrayBlockingQueue(querySize))
        }
    }

    /**
     * 线程池任务队列是否为空
     *
     * @return 任务队列是否为空
     */
    fun isEmpty(): Boolean = this.executor.queue.isEmpty()

    @Synchronized
    fun stop() {
        this.executor.shutdown()
        val start = System.currentTimeMillis()
        try {
            while (!this.executor.awaitTermination(1, TimeUnit.SECONDS)) {
                log.info("pool [$poolName] there are still unfinished tasks...")
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        val end = System.currentTimeMillis()
        threadPoolInstanceMap.remove(poolName)
        log.info("thread pool [" + poolName + "] is stoped. It took " + (end - start) + " millisecond")
    }

    /**
     * 销毁线程池
     */
    @Synchronized
    fun destroy() {
        this.executor.shutdownNow()
        threadPoolInstanceMap.remove(poolName)
        log.info("thread pool [$poolName] is destroyed")
    }

    /**
     * 增加新的任务 每增加一个新任务,都要唤醒任务队列
     *
     * @param newTask 任务
     */
    fun addTask(newTask: BaseAsyncTask): Future<Any> {
        newTask.submitTime = CommonUtils.getNowDateTime()
        log.debug("thread pool [$poolName] submit task[" + newTask.taskId + "]: " + newTask.taskName)
        return this.executor.submit(newTask)
    }

    /**
     * 批量增加新任务
     *
     * @param tasks 任务
     */
    fun batchAddTask(tasks: Array<BaseAsyncTask>) {
        if (tasks.isEmpty()) {
            return
        }
        for (task in tasks) {
            task.submitTime = CommonUtils.getNowDateTime()
            this.executor.submit(task)
            log.debug("thread pool [$poolName] submit task[" + task.taskId + "]: " + task.taskName)
        }
    }

    companion object {

        private val log = LogFactory.getInstance(ThreadPoolService::class.java)// 日志对象

        private val threadPoolInstanceMap = ConcurrentHashMap<String, ThreadPoolService>()

        /**
         * 获取线程池实例
         *
         * @param minThreadNumber 最小线程数
         * @param maxThreadNumber 最大线程数
         * @param querySize       队列容量
         * @param poolName        线程池实例名
         * @param maxFreeTime     线程最大空闲时间
         * @return 线程池实例
         */
        @JvmStatic
        @JvmOverloads
        fun getInstance(minThreadNumber: Int, maxThreadNumber: Int, querySize: Int, poolName: String = "defaultThreadPool", maxFreeTime: Long = 6000): ThreadPoolService {
            val instance: ThreadPoolService
            synchronized(ThreadPoolService::class.java) {
                if (!threadPoolInstanceMap.containsKey(poolName)) {
                    instance = ThreadPoolService(poolName, maxFreeTime, minThreadNumber, maxThreadNumber, querySize)
                    log.debug("init ThreadPool [$poolName] success, minThread:$minThreadNumber maxThread:$maxThreadNumber")
                    threadPoolInstanceMap.put(poolName, instance)
                } else {
                    instance = threadPoolInstanceMap.getValue(poolName)
                }
            }
            return instance
        }

        @JvmStatic
        @Synchronized
        fun stopAll() {
            synchronized(ThreadPoolService::class.java) {
                threadPoolInstanceMap.forEach { (_, poolService) -> poolService.stop() }
            }
        }

        /**
         * 销毁所有线程池
         */
        @JvmStatic
        @Synchronized
        fun destroyAll() {
            synchronized(ThreadPoolService::class.java) {
                threadPoolInstanceMap.forEach { (_, poolService) -> poolService.destroy() }
            }
        }
    }

}
