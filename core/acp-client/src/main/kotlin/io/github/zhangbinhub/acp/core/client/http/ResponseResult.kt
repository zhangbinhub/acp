package io.github.zhangbinhub.acp.core.client.http

import okhttp3.Response

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
data class ResponseResult(
    var status: Int = 0,
    var headers: MutableMap<String, List<String>>,
    var body: String? = null,
    var response: Response
) {
    override fun toString(): String {
        return "status=$status,body=$body"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseResult

        if (status != other.status) return false
        if (headers != other.headers) return false
        if (body != other.body) return false
        if (response != other.response) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + headers.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + response.hashCode()
        return result
    }

}