package pers.acp.spring.boot.listener

import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import pers.acp.spring.boot.base.BaseInitialization
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * SpringBoot 应用初始化
 *
 * @author zhangbin by 2018-1-31 12:50
 * @since JDK 11
 */
class AcpApplicationStartupListener(private val logAdapter: LogAdapter) : ApplicationListener<ApplicationStartedEvent> {

    /**
     * 监听系统事件
     *
     * @param event 事件对象
     */
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        sortMap(event.applicationContext.getBeansOfType(BaseInitialization::class.java)).forEach { entry ->
            logAdapter.info("start system initialization[" + entry.value.order + "] : " + entry.value.name)
            entry.value.start()
        }
    }

    private fun sortMap(srcMap: MutableMap<String, BaseInitialization>): List<MutableMap.MutableEntry<String, BaseInitialization>> {
        if (srcMap.isEmpty()) {
            return listOf()
        }
        return srcMap.entries.toList().sortedBy { it.value.order }
    }

}
