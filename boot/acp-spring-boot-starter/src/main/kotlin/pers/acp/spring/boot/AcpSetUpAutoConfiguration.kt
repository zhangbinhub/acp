package pers.acp.spring.boot

import io.netty.handler.codec.ByteToMessageDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask
import pers.acp.spring.boot.component.SystemControl
import pers.acp.spring.boot.component.TimerTaskSchedulerCtrl
import pers.acp.spring.boot.conf.AcpCoreConfiguration
import pers.acp.spring.boot.conf.ScheduleConfiguration
import pers.acp.spring.boot.conf.TcpServerConfiguration
import pers.acp.spring.boot.conf.UdpServerConfiguration
import pers.acp.spring.boot.init.InitServer
import pers.acp.spring.boot.init.SystemInitialization
import pers.acp.spring.boot.init.task.InitTcpServer
import pers.acp.spring.boot.init.task.InitUdpServer
import pers.acp.spring.boot.interfaces.Listener
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.interfaces.TimerTaskScheduler
import pers.acp.spring.boot.listener.AcpApplicationStartupListener
import pers.acp.spring.boot.socket.base.ISocketServerHandle

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