package com.github.zhangbinhub.acp.boot.conf

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Controller 切面日志配置
 *
 * @author zhangbin by 14/04/2018 00:36
 * @since JDK 11
 */
@ConfigurationProperties(prefix = "acp.controller-log")
class ControllerLogConfiguration {

    /**
     * 是否启用，默认：true
     */
    var enabled = true

    /**
     * 不进行日志记录的 url 正则表达式
     */
    var noLogUriRegular: MutableList<String> = mutableListOf()

}
