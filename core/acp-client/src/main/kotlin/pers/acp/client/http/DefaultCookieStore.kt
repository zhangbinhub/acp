package pers.acp.client.http

import okhttp3.Cookie
import okhttp3.HttpUrl
import pers.acp.client.http.interfaces.CookieStore
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class DefaultCookieStore : CookieStore {

    private val concurrentHashMap = ConcurrentHashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        concurrentHashMap[url.host()] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = concurrentHashMap[url.host()]
        return cookies ?: ArrayList()
    }

}