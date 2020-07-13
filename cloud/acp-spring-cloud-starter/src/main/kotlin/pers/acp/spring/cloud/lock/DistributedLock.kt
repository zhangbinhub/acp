package pers.acp.spring.cloud.lock

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
interface DistributedLock {

    /**
     * 获取分布式锁
     *
     * @param lockId   锁ID
     * @param clientId 客户端ID
     * @param timeOut  锁超时时间，单位毫秒
     * @param reentrant 是否可重入
     * @return true|false
     */
    fun getLock(lockId: String, clientId: String, timeOut: Long, reentrant: Boolean = true): Boolean

    /**
     * 释放分布式锁
     *
     * @param lockId   锁ID
     * @param clientId 客户端ID
     */
    fun releaseLock(lockId: String, clientId: String)

}
