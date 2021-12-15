package com.github.zhangbinhub.acp.cloud.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 日志服务服务端配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.cloud.log-server")
class AcpCloudLogServerConfiguration {

    /**
     * 消费日志消息的组id，多个日志服务使用相同的组id，能够保证日志消息不被重复消费，默认："acp_cloud_log_server_group_id"
     */
    var groupId = "acp_cloud_log_server_group_id"

    /**
     * 当前服务是否是日志服务，默认：false
     */
    var enabled = false

    /**
     * 日志消息的topic名称（队列名称），默认："acp_cloud_log_server_message_topic"
     */
    var destination = "acp_cloud_log_server_message_topic"

}
