package io.github.zhangbinhub.acp.cloud

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.cloud.function.context.FunctionProperties
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.util.MimeTypeUtils
import io.github.zhangbinhub.acp.cloud.log.LogConstant

/**
 * 日志服务自动配置
 *
 * @author zhang by 14/01/2019 14:42
 * @since JDK 11
 */
class AcpCloudLogServerEnvironmentPostProcessor : EnvironmentPostProcessor {
    private val overridePropertiesName = "acpCloudLogServerOverridesProperties"
    private val defaultPropertiesName = "acpCloudLogServerDefaultProperties"
    private val logServerEnabled = "acp.cloud.log-server.enabled"
    private val logServerClientEnabled = "acp.cloud.log-server.client.enabled"
    private val logServerDestination = "acp.cloud.log-server.destination"
    private val logServerGroupId = "acp.cloud.log-server.group-id"
    private val functionDefinitionProperties = "${FunctionProperties.PREFIX}.definition"
    private val logConsumerBindName = "${LogConstant.CONSUMER}-in-0"
    private val defaultDestination = "acp_cloud_log_server_message_topic"
    private val defaultGroupId = "acp_cloud_log_server_group_id"

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {
        if (environment!!.containsProperty(logServerEnabled)) {
            if ("true".equals(environment.getProperty(logServerEnabled), ignoreCase = true)) {
                val overrides: MutableMap<String, Any> = HashMap()
                var definition = LogConstant.CONSUMER
                if (environment.containsProperty(functionDefinitionProperties)) {
                    val property = environment.getProperty(functionDefinitionProperties)
                    if (property != null && property.contains(definition)) {
                        return
                    }
                    definition = "$property;$definition"
                }
                overrides[functionDefinitionProperties] = definition
                addOrReplace(environment.propertySources, overrides, overridePropertiesName, true)

                val default: MutableMap<String, Any> = HashMap()
                "spring.cloud.stream".let { prefix ->
                    default["$prefix.function.bindings.$logConsumerBindName"] = LogConstant.INPUT
                    val destination = environment.getProperty(logServerDestination, defaultDestination)
                    val groupId = environment.getProperty(logServerGroupId, defaultGroupId)
                    default["$prefix.bindings.${LogConstant.INPUT}.destination"] = destination
                    default["$prefix.bindings.${LogConstant.INPUT}.contentType"] = MimeTypeUtils.APPLICATION_JSON_VALUE
                    default["$prefix.bindings.${LogConstant.INPUT}.group"] = groupId
                    addOrReplace(environment.propertySources, default, defaultPropertiesName, false)
                }
            }
        }
        if (environment.containsProperty(logServerClientEnabled)) {
            if ("true".equals(environment.getProperty(logServerClientEnabled), ignoreCase = true)) {
                val default: MutableMap<String, Any> = HashMap()
                "spring.cloud.stream".let { prefix ->
                    val destination = environment.getProperty(logServerDestination, defaultDestination)
                    default["$prefix.bindings.${LogConstant.OUTPUT}.destination"] = destination
                    default["$prefix.bindings.${LogConstant.OUTPUT}.contentType"] = MimeTypeUtils.APPLICATION_JSON_VALUE
                    addOrReplace(environment.propertySources, default, defaultPropertiesName, false)
                }
            }
        }
    }

    private fun addOrReplace(
        propertySources: MutablePropertySources, map: Map<String, Any?>,
        propertySourceName: String, first: Boolean
    ) {
        var target: MapPropertySource? = null
        if (propertySources.contains(propertySourceName)) {
            val source = propertySources[propertySourceName]
            if (source is MapPropertySource) {
                target = source
                for (key in map.keys) {
                    if (!target.containsProperty(key)) {
                        target.source[key] = map[key]
                    }
                }
            }
        }
        if (target == null) {
            target = MapPropertySource(propertySourceName, map)
        }
        if (!propertySources.contains(propertySourceName)) {
            if (first) {
                propertySources.addFirst(target)
            } else {
                propertySources.addLast(target)
            }
        }
    }
}
