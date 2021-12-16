package io.github.zhangbinhub.acp.boot.init

import io.netty.handler.codec.ByteToMessageDecoder
import io.github.zhangbinhub.acp.boot.socket.base.ISocketServerHandle
import java.util.concurrent.ConcurrentHashMap

/**
 * 初始化任务基类
 *
 * @author zhang by 22/06/2019
 * @since JDK 11
 */
abstract class BaseInitTask {
    private val socketServerHandleMap = ConcurrentHashMap<String, ISocketServerHandle>()

    private val byteToMessageDecoderMap = ConcurrentHashMap<String, ByteToMessageDecoder>()

    protected fun addServerHandle(socketServerHandle: ISocketServerHandle) {
        socketServerHandleMap[socketServerHandle.javaClass.canonicalName] = socketServerHandle
    }

    protected fun addMessageDecoder(byteToMessageDecoder: ByteToMessageDecoder) {
        byteToMessageDecoderMap[byteToMessageDecoder.javaClass.canonicalName] = byteToMessageDecoder
    }

    protected fun getSocketServerHandle(className: String?): ISocketServerHandle? {
        return socketServerHandleMap[className]
    }

    protected fun getMessageDecoder(className: String?): ByteToMessageDecoder? {
        return byteToMessageDecoderMap[className]
    }
}
