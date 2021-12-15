package io.github.zhangbinhub.acp.cloud.log.consumer

import io.github.zhangbinhub.acp.cloud.log.LogInfo

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
interface LogProcess {

    fun process(logInfo: LogInfo)

}
