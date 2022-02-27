package io.github.zhangbinhub.acp.core.client.http

import io.github.zhangbinhub.acp.core.CommonTools
import okhttp3.RequestBody

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
data class RequestParam(
    var url: String,
    val requestHeaders: MutableMap<String, String>,
    var clientCharset: String = CommonTools.getDefaultCharset(),
    var basicUsername: String? = null,
    var basicPassword: String? = null,
    var params: MutableMap<String, String> = mutableMapOf(),
    var bodyString: String = "",
    var bodyBytes: ByteArray? = null,
    var body: RequestBody? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestParam

        if (url != other.url) return false
        if (requestHeaders != other.requestHeaders) return false
        if (clientCharset != other.clientCharset) return false
        if (basicUsername != other.basicUsername) return false
        if (basicPassword != other.basicPassword) return false
        if (params != other.params) return false
        if (bodyString != other.bodyString) return false
        if (bodyBytes != null) {
            if (other.bodyBytes == null) return false
            if (!bodyBytes!!.contentEquals(other.bodyBytes!!)) return false
        } else if (other.bodyBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + requestHeaders.hashCode()
        result = 31 * result + clientCharset.hashCode()
        result = 31 * result + (basicUsername?.hashCode() ?: 0)
        result = 31 * result + (basicPassword?.hashCode() ?: 0)
        result = 31 * result + params.hashCode()
        result = 31 * result + bodyString.hashCode()
        result = 31 * result + (bodyBytes?.contentHashCode() ?: 0)
        return result
    }
}
