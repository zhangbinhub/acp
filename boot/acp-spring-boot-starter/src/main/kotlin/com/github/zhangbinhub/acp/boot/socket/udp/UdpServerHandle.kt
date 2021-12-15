package com.github.zhangbinhub.acp.boot.socket.udp

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.socket.DatagramPacket
import com.github.zhangbinhub.acp.boot.socket.base.SocketServerHandle
import com.github.zhangbinhub.acp.boot.conf.SocketListenerConfiguration
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import com.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle

/**
 * Udp 报文处理
 *
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
class UdpServerHandle internal
constructor(logAdapter: LogAdapter,
            socketListenerConfiguration: SocketListenerConfiguration,
            socketServerHandle: ISocketServerHandle) : SocketServerHandle(logAdapter, socketListenerConfiguration, socketServerHandle) {

    override fun beforeReadMessage(msg: Any): ByteBuf = (msg as DatagramPacket).content()

    override fun afterSendMessage(ctx: ChannelHandlerContext) {}

    override fun afterReadMessage(ctx: ChannelHandlerContext) {}

    @Throws(Exception::class)
    override fun beforeSendMessage(ctx: ChannelHandlerContext, requestMsg: Any?, sendStr: String): Any {
        val packet = requestMsg as DatagramPacket?
        return DatagramPacket(Unpooled.copiedBuffer(sendStr.toByteArray(charset(socketListenerConfiguration.charset))), packet!!.sender())
    }

}
