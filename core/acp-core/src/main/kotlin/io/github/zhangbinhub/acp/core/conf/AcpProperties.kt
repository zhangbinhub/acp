package io.github.zhangbinhub.acp.core.conf

import io.github.zhangbinhub.acp.core.base.BaseProperties
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.tools.CommonUtils

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class AcpProperties : BaseProperties() {
    companion object {

        private val log = LogFactory.getInstance(AcpProperties::class.java)

        /**
         * 获取数据库配置实例
         *
         * @return 数据库配置实例
         */
        fun getInstance(): AcpProperties? =
                try {
                    val propertiesFileName = "/acp.properties"
                    getInstance(AcpProperties::class.java, propertiesFileName, CommonUtils.getWebRootAbsPath() + propertiesFileName) as AcpProperties
                } catch (e: Exception) {
                    log.error(e.message, e)
                    null
                }
    }

}
