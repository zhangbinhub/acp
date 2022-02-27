package io.github.zhangbinhub.acp.boot.socket.udp

import io.github.zhangbinhub.acp.boot.conf.SocketListenerConfiguration
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle
import io.github.zhangbinhub.acp.core.interfaces.IDaemonService
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel

/**
 * Udp 服务端
 */
class UdpServer
/**
 * 构造函数
 *
 * @param logAdapter                         日志适配器
 * @param port                        端口
 * @param socketListenerConfiguration 监听服务配置
 * @param socketServerHandle          接收报文处理对象
 */
    (
    private val logAdapter: LogAdapter,
    private val port: Int,
    private val socketListenerConfiguration: SocketListenerConfiguration,
    private val socketServerHandle: ISocketServerHandle?
) : IDaemonService, Runnable {

    private val bossGroup: EventLoopGroup

    init {
        this.bossGroup = NioEventLoopGroup()
    }

    override fun run() {
        if (socketServerHandle != null) {
            try {
                Bootstrap().group(bossGroup)
                    .channel(NioDatagramChannel::class.java)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(object : ChannelInitializer<NioDatagramChannel>() {
                        override fun initChannel(ch: NioDatagramChannel) {
                            ch.pipeline()
                                .addLast(UdpServerHandle(logAdapter, socketListenerConfiguration, socketServerHandle))
                        }
                    }).bind(port).sync().channel().closeFuture().sync()
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
            }

        } else {
            logAdapter.error("udp listen server is stop,case by:response object is null[BaseSocketHandle]")
        }
    }

    override fun getServiceName(): String = socketListenerConfiguration.name

    override fun stopService() {
        bossGroup.shutdownGracefully()
    }

}
