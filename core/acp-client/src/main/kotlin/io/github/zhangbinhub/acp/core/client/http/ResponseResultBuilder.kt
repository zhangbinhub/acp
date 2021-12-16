package io.github.zhangbinhub.acp.core.client.http

import okhttp3.Headers
import okhttp3.Response

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
class ResponseResultBuilder(val response: Response) {

    var status: Int = 0
        private set

    var headers: MutableMap<String, List<String>> = mutableMapOf()
        private set

    var bodyString: String? = null

    fun status(status: Int): ResponseResultBuilder {
        this.status = status
        return this
    }

    fun headers(headers: Headers): ResponseResultBuilder {
        this.headers = headers.toMultimap()
        return this
    }

    fun bodyString(bodyString: String): ResponseResultBuilder {
        this.bodyString = bodyString
        return this
    }

    fun build(): ResponseResult = ResponseResult(
            status = this.status,
            headers = this.headers,
            body = this.bodyString,
            response = this.response
    )

}