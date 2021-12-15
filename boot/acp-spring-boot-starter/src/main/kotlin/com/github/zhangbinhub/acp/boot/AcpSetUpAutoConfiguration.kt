package com.github.zhangbinhub.acp.boot

import io.netty.handler.codec.ByteToMessageDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import com.github.zhangbinhub.acp.boot.base.BaseSpringBootScheduledAsyncTask
import com.github.zhangbinhub.acp.boot.component.SystemControl
import com.github.zhangbinhub.acp.boot.component.TimerTaskSchedulerCtrl
import com.github.zhangbinhub.acp.boot.conf.AcpCoreConfiguration
import com.github.zhangbinhub.acp.boot.conf.ScheduleConfiguration
import com.github.zhangbinhub.acp.boot.conf.TcpServerConfiguration
import com.github.zhangbinhub.acp.boot.conf.UdpServerConfiguration
import com.github.zhangbinhub.acp.boot.init.InitServer
import com.github.zhangbinhub.acp.boot.init.SystemInitialization
import com.github.zhangbinhub.acp.boot.init.task.InitTcpServer
import com.github.zhangbinhub.acp.boot.init.task.InitUdpServer
import com.github.zhangbinhub.acp.boot.interfaces.Listener
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import com.github.zhangbinhub.acp.boot.interfaces.TimerTaskScheduler
import com.github.zhangbinhub.acp.boot.listener.AcpApplicationStartupListener
import com.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class AcpSetUpAutoConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Autowired(required = false)
    fun timerTaskScheduler(logAdapter: LogAdapter,
                           properties: TaskSchedulingProperties,
                           scheduleConfiguration: ScheduleConfiguration,
                           baseSpringBootScheduledTaskMap: Map<String, BaseSpringBootScheduledAsyncTask>) = TimerTaskSchedulerCtrl(logAdapter, properties, scheduleConfiguration, baseSpringBootScheduledTaskMap)

    @Bean
    @Autowired(required = false)
    fun initTcpServer(logAdapter: LogAdapter,
                      tcpServerConfiguration: TcpServerConfiguration,
                      socketServerHandleList: List<ISocketServerHandle>,
                      byteToMessageDecoderList: List<ByteToMessageDecoder>) = InitTcpServer(logAdapter, tcpServerConfiguration, socketServerHandleList, byteToMessageDecoderList)

    @Bean
    @Autowired(required = false)
    fun initUdpServer(logAdapter: LogAdapter,
                      udpServerConfiguration: UdpServerConfiguration,
                      socketServerHandleList: List<ISocketServerHandle>) = InitUdpServer(logAdapter, udpServerConfiguration, socketServerHandleList)

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun initServer(logAdapter: LogAdapter,
                   initTcpServer: InitTcpServer,
                   initUdpServer: InitUdpServer,
                   acpCoreConfiguration: AcpCoreConfiguration) = InitServer(logAdapter, initTcpServer, initUdpServer, acpCoreConfiguration)

    @Bean
    fun systemInitialization(logAdapter: LogAdapter,
                             systemControl: SystemControl,
                             initServer: InitServer) = SystemInitialization(logAdapter, systemControl, initServer)

    @Bean
    fun acpApplicationStartupListener(logAdapter: LogAdapter) = AcpApplicationStartupListener(logAdapter)

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Autowired(required = false)
    fun systemControl(logAdapter: LogAdapter,
                      listenerMap: Map<String, Listener>?,
                      timerTaskScheduler: TimerTaskScheduler) = SystemControl(logAdapter, listenerMap, timerTaskScheduler)

}