package pers.acp.client.socket.base

import io.netty.handler.timeout.IdleStateEvent

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
interface ISocketClientHandle {
    /**
     * 接收消息
     *
     * @param recvStr 接收的字符串
     */
    fun receiveMsg(recvStr: String)

    /**
     * 事件触发
     *
     * @param evt 事件
     * evt.state() IdleState
     * @return 发送内容
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun userEventTriggered(evt: IdleStateEvent): String
}