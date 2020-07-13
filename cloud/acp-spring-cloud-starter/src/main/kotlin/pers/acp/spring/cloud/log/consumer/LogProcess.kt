package pers.acp.spring.cloud.log.consumer

import pers.acp.spring.cloud.log.LogInfo

/**
 * @author zhang by 25/03/2019
 * @since JDK 11
 */
interface LogProcess {

    fun process(logInfo: LogInfo)

}
