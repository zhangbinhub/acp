package io.github.zhangbinhub.acp.boot.conf

import io.github.zhangbinhub.acp.core.CommonTools

/**
 * Socket监听配置
 */
class SocketListenerConfiguration {

    /**
     * 名称
     */
    var name: String = "Default Socket Listener"

    /**
     * 线程数
     */
    var threadNumber = 0

    /**
     * 是否启用
     */
    var enabled = false

    /**
     * 是否长连接
     */
    var keepAlive = false

    /**
     * 空闲等待时间（单位毫秒）
     */
    var idletime: Long = 10000

    /**
     * 监听端口
     */
    var port: Int = 0

    /**
     * 是否16进制报文
     */
    var hex = false

    /**
     * 消息解码器
     */
    var messageDecoder: String? = null

    /**
     * 消息处理实例（类名）
     */
    var handleBean: String? = null

    /**
     * 是否需要响应
     */
    var responsable = true

    /**
     * 字符编码
     */
    var charset = CommonTools.getDefaultCharset()

}
