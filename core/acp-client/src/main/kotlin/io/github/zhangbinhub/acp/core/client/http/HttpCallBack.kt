package io.github.zhangbinhub.acp.core.client.http

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

import java.io.IOException

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
abstract class HttpCallBack : Callback {

    override fun onFailure(call: Call, e: IOException) {
        onRequestFailure(call, e)
    }

    @Throws(IOException::class)
    override fun onResponse(call: Call, response: Response) {
        onRequestResponse(call, AcpClient.parseResponseResult(response))
    }

    abstract fun onRequestFailure(call: Call, e: IOException)

    abstract fun onRequestResponse(call: Call, responseResult: ResponseResult)

}
