package pers.acp.spring.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Tcp 服务端配置
 */
@ConfigurationProperties(prefix = "acp.tcp-server")
class TcpServerConfiguration {

    /**
     * Socket 监听列表
     */
    var listeners: MutableList<SocketListenerConfiguration> = mutableListOf()

}
