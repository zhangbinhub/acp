package io.github.zhangbinhub.acp.boot.listener

import io.github.zhangbinhub.acp.boot.base.BaseInitialization
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener

/**
 * SpringBoot 应用初始化
 *
 * @author zhangbin by 2018-1-31 12:50
 * @since JDK 11
 */
class AcpApplicationStartupListener(
    private val logAdapter: LogAdapter,
    private val initializationMap: Map<String, BaseInitialization>
) : ApplicationListener<ApplicationStartedEvent> {

    /**
     * 监听系统事件
     *
     * @param event 事件对象
     */
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        sortMap(initializationMap).forEach { entry ->
            logAdapter.info("start system initialization[" + entry.value.order + "] : " + entry.value.name)
            entry.value.start()
        }
    }

    private fun sortMap(srcMap: Map<String, BaseInitialization>): List<Map.Entry<String, BaseInitialization>> {
        if (srcMap.isEmpty()) {
            return listOf()
        }
        return srcMap.entries.toList().sortedBy { it.value.order }
    }

}
