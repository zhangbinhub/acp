package io.github.zhangbinhub.acp.core.client.http

import okhttp3.RequestBody
import io.github.zhangbinhub.acp.core.CommonTools

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
class RequestParamBuilder(var url: String) {

    var clientCharset = CommonTools.getDefaultCharset()
        private set

    var basicUsername: String? = null
        private set

    var basicPassword: String? = null
        private set

    val requestHeaders = mutableMapOf<String, String>()

    var params = mutableMapOf<String, String>()
        private set

    var bodyString = ""
        private set

    var bodyBytes: ByteArray? = null
        private set

    var body: RequestBody? = null
        private set

    fun addRequestHeader(name: String, value: String): RequestParamBuilder {
        this.requestHeaders[name] = value
        return this
    }

    fun url(url: String): RequestParamBuilder {
        this.url = url
        return this
    }

    fun clientCharset(clientCharset: String): RequestParamBuilder {
        this.clientCharset = clientCharset
        return this
    }

    fun basicUsername(basicUsername: String): RequestParamBuilder {
        this.basicUsername = basicUsername
        return this
    }

    fun basicPassword(basicPassword: String): RequestParamBuilder {
        this.basicPassword = basicPassword
        return this
    }

    fun body(body: String): RequestParamBuilder {
        this.bodyString = body
        return this
    }

    fun body(body: ByteArray): RequestParamBuilder {
        this.bodyBytes = body
        return this
    }

    fun body(body: RequestBody): RequestParamBuilder {
        this.body = body
        return this
    }

    fun params(params: MutableMap<String, String>): RequestParamBuilder {
        this.params = params
        return this
    }

    fun build(): RequestParam = RequestParam(
            url = this.url,
            requestHeaders = this.requestHeaders,
            clientCharset = this.clientCharset,
            basicUsername = this.basicUsername,
            basicPassword = this.basicPassword,
            params = this.params,
            bodyString = this.bodyString,
            bodyBytes = this.bodyBytes,
            body = this.body
    )

}