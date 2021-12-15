package io.github.zhangbinhub.acp.cloud.log

import org.springframework.beans.factory.annotation.Value

/**
 * 日志消息实体
 *
 * @author zhangbin by 11/07/2018 13:34
 * @since JDK 11
 */
class LogInfo {
    @Value("\${spring.application.name}")
    val serverName: String? = null
    var serverIp: String? = null
    var serverPort: Int? = null

    /**
     * 日志类型字符串
     * 在 log-server 的 logback.xml 中对应配置日志规则，可实现不同类型的日志记录到的文件
     *
     * @see LogConstant.DEFAULT_TYPE
     * 默认日志类型为“ALL”，新增日志类型之后需在 log-server 中的 logback.xml 参照 ALL 进行配置
     */
    var logType = LogConstant.DEFAULT_TYPE

    var logLevel: String? = null
        internal set

    var serverTime: Long? = null

    var className: String? = null

    var lineNo: Int = 0

    var message: String? = null
        internal set

    var params: List<Any?> = listOf()
        internal set
}
