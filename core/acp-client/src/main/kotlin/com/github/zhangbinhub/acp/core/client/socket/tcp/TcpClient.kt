package com.github.zhangbinhub.acp.core.client.socket.tcp

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import com.github.zhangbinhub.acp.core.client.socket.base.SocketClient
import com.github.zhangbinhub.acp.core.CommonTools
import com.github.zhangbinhub.acp.core.log.LogFactory
import java.nio.CharBuffer
import java.util.concurrent.TimeUnit

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class TcpClient(serverIp: String, port: Int, timeOut: Int, idleTime: Int) : SocketClient(serverIp, port, timeOut) {

    private val log = LogFactory.getInstance(this.javaClass)

    private var idleTime: Int = 0

    var keepAlive = false

    var needRead = true

    init {
        if (idleTime < maxTime) {
            this.idleTime = idleTime
        } else {
            this.idleTime = maxTime
        }
    }

    /**
     * 创建链接
     */
    override fun connect() {
        group = NioEventLoopGroup()
        try {
            channel = Bootstrap().group(group)
                    .channel(NioSocketChannel::class.java)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                    .handler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().apply {
                                messageDecoder?.let {
                                    this.addLast(it)
                                }
                            }.addLast(IdleStateHandler(idleTime.toLong(), idleTime.toLong(), idleTime.toLong(), TimeUnit.MILLISECONDS),
                                    this@TcpClient)
                        }
                    }).apply {
                        if (keepAlive) {
                            this.option(ChannelOption.SO_KEEPALIVE, true)
                        }
                    }.connect(serverIp, port).sync().channel()
        } catch (e: Exception) {
            log.error(e.message, e)
            group?.shutdownGracefully()
        }

    }

    override fun beforeSendMessage(sendStr: String): Any =
            ByteBufUtil.encodeString(channel!!.alloc(), CharBuffer.wrap(sendStr), charset(serverCharset))

    override fun afterSendMessage(channel: Channel) {
        if (!keepAlive && !needRead) {
            try {
                channel.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
            doClose()
        }
    }

    override fun beforeReadMessage(msg: Any): ByteBuf = msg as ByteBuf

    override fun afterReadMessage(ctx: ChannelHandlerContext) {
        if (!keepAlive) {
            try {
                ctx.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
            doClose()
        }
    }

    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        socketHandle?.let {
            val sendStr = it.userEventTriggered(evt as IdleStateEvent)
            if (!CommonTools.isNullStr(sendStr)) {
                doSend(sendStr)
            }
        }
    }

}