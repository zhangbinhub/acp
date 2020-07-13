package pers.acp.spring.boot.socket.tcp

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.IdleStateEvent
import pers.acp.core.CommonTools
import pers.acp.spring.boot.socket.base.SocketServerHandle
import pers.acp.spring.boot.conf.SocketListenerConfiguration
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.socket.base.ISocketServerHandle

import java.nio.CharBuffer

/**
 * Tcp 报文处理
 *
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
class TcpServerHandle internal
constructor(logAdapter: LogAdapter,
            socketListenerConfiguration: SocketListenerConfiguration,
            socketServerHandle: ISocketServerHandle) : SocketServerHandle(logAdapter, socketListenerConfiguration, socketServerHandle) {

    override fun beforeReadMessage(msg: Any): ByteBuf = msg as ByteBuf

    override fun afterSendMessage(ctx: ChannelHandlerContext) {
        if (!socketListenerConfiguration.keepAlive) {
            ctx.close()
        }
    }

    override fun afterReadMessage(ctx: ChannelHandlerContext) {
        if (!socketListenerConfiguration.keepAlive) {
            ctx.close()
        }
    }

    @Throws(Exception::class)
    override fun beforeSendMessage(ctx: ChannelHandlerContext, requestMsg: Any?, sendStr: String): Any {
        return ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(sendStr), charset(socketListenerConfiguration.charset))
    }

    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        val event = evt as IdleStateEvent
        val idleStr = socketServerHandle.userEventTriggered(event)
        if (!CommonTools.isNullStr(idleStr)) {
            doResponse(ctx, null, idleStr!!)
        }
    }

}
