package io.github.zhangbinhub.acp.core.client.socket.udp

import io.github.zhangbinhub.acp.core.client.socket.base.SocketClient
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import java.net.InetSocketAddress

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class UdpClient(serverIp: String, port: Int, timeOut: Int) : SocketClient(serverIp, port, timeOut) {

    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 创建链接
     */
    override fun connect() {
        group = NioEventLoopGroup()
        try {
            channel = Bootstrap().group(group!!)
                .channel(NioDatagramChannel::class.java)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(object : ChannelInitializer<NioDatagramChannel>() {
                    override fun initChannel(ch: NioDatagramChannel) {
                        ch.pipeline().addLast(this@UdpClient)
                    }
                }).connect(serverIp, port).sync().channel()
        } catch (e: Exception) {
            log.error(e.message, e)
            group?.shutdownGracefully()
        }

    }

    @Throws(Exception::class)
    override fun beforeSendMessage(sendStr: String): Any =
        DatagramPacket(
            Unpooled.copiedBuffer(sendStr.toByteArray(charset(serverCharset))),
            InetSocketAddress(serverIp, port)
        )

    override fun afterSendMessage(channel: Channel) {}

    override fun beforeReadMessage(msg: Any): ByteBuf = (msg as DatagramPacket).content()

    override fun afterReadMessage(ctx: ChannelHandlerContext) = doClose()

}
