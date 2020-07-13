package pers.acp.client.socket.base

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.EventLoopGroup
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.util.ReferenceCountUtil
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import java.nio.charset.Charset

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
abstract class SocketClient(internal var serverIp: String, internal var port: Int, timeOut: Int) : ChannelInboundHandlerAdapter() {

    private val lock = Any()

    private val log = LogFactory.getInstance(this.javaClass)

    protected var timeOut: Int = 0

    protected val maxTime = 3600000

    protected var group: EventLoopGroup? = null

    protected var channel: Channel? = null

    var hex = false

    var serverCharset = CommonTools.getDefaultCharset()

    var messageDecoder: ByteToMessageDecoder? = null

    var socketHandle: ISocketClientHandle? = null

    private val isClosed: Boolean
        get() =
            channel == null || group == null || !channel!!.isOpen || group!!.isShuttingDown || group!!.isShutdown || group!!.isTerminated

    init {
        if (timeOut < maxTime) {
            this.timeOut = timeOut
        } else {
            this.timeOut = maxTime
        }
    }

    fun doSend(requestStr: String) =
            try {
                synchronized(lock) {
                    if (isClosed) {
                        connect()
                    }
                }
                var sendStr = requestStr
                if (hex) {
                    val bts = ByteBufUtil.decodeHexDump(sendStr)
                    sendStr = String(bts, charset(serverCharset))
                }
                val msgPack = beforeSendMessage(sendStr)
                channel!!.writeAndFlush(msgPack)
                afterSendMessage(channel!!)
            } catch (e: Exception) {
                log.error(e.message, e)
            }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        try {
            val byteBuf = beforeReadMessage(msg)
            val recvStr = if (hex) {
                ByteBufUtil.hexDump(byteBuf)
            } else {
                byteBuf.toString(charset(serverCharset))
            }
            log.debug("socket receive:$recvStr")
            socketHandle?.receiveMsg(recvStr)
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            ReferenceCountUtil.release(msg)
            afterReadMessage(ctx)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error(cause.message, cause)
        ctx.close()
    }

    internal abstract fun connect()

    @Throws(Exception::class)
    internal abstract fun beforeSendMessage(sendStr: String): Any

    internal abstract fun afterSendMessage(channel: Channel)

    internal abstract fun beforeReadMessage(msg: Any): ByteBuf

    internal abstract fun afterReadMessage(ctx: ChannelHandlerContext)

    fun doClose() {
        group?.shutdownGracefully()
    }

}
