package pers.acp.spring.boot.socket.base

import io.netty.handler.timeout.IdleStateEvent

/**
 * Socket 报文处理接口
 */
interface ISocketServerHandle {

    /**
     * 对接收到的报文进行处理
     *
     * @param recvStr 接收到的报文
     * @return 返回报文
     */
    fun doResponse(recvStr: String): String

    /**
     * 事件触发
     *
     * @param evt 事件
     * evt.state() IdleState
     * @return 发送内容
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun userEventTriggered(evt: IdleStateEvent): String?

}
