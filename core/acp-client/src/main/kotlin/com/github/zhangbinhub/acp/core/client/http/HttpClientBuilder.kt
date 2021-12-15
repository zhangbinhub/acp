package com.github.zhangbinhub.acp.core.client.http

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import com.github.zhangbinhub.acp.core.client.exceptions.HttpException
import com.github.zhangbinhub.acp.core.client.http.interfaces.CookieStore
import com.github.zhangbinhub.acp.core.client.http.interfaces.HttpInterceptor

import java.util.concurrent.TimeUnit

/**
 * http 客户端
 */
class HttpClientBuilder {

    /**
     * 是否开启无效的SSL证书验证
     */
    private var disableSslValidation = false

    /**
     * 最大链接数
     */
    private var maxTotalConn = 1_000

    /**
     * 连接超时时间，单位毫秒
     */
    private var connectTimeOut = 10_000

    /**
     * 读取超时时间，单位毫秒
     */
    private var readTimeOut = 60_000

    /**
     * 写超时时间，单位毫秒
     */
    private var writeTimeOut = 60_000

    /**
     * 链接空闲时间，单位毫秒
     */
    private var timeToLive = 300_000L

    /**
     * 时间单位
     */
    private var timeToLiveTimeUnit = TimeUnit.MILLISECONDS

    /**
     * 跟踪重定向
     */
    private var followRedirects = true

    /**
     * SSL链接类型
     */
    private var sslProtocolVersion = "SSL"

    /**
     * 连接失败时是否重试
     */
    private var retryOnConnectionFailure = true

    private val httpInterceptorList: MutableList<HttpInterceptor> = mutableListOf()

    private var cookieStore: CookieStore = DefaultCookieStore()

    fun maxTotalConn(maxTotalConn: Int): HttpClientBuilder {
        this.maxTotalConn = maxTotalConn
        return this
    }

    fun connectTimeOut(timeOut: Int): HttpClientBuilder {
        this.connectTimeOut = timeOut
        return this
    }

    fun readTimeOut(readTimeOut: Int): HttpClientBuilder {
        this.readTimeOut = readTimeOut
        return this
    }

    fun writeTimeOut(writeTimeout: Int): HttpClientBuilder {
        this.writeTimeOut = writeTimeout
        return this
    }

    fun timeToLive(timeToLive: Long): HttpClientBuilder {
        this.timeToLive = timeToLive
        return this
    }

    fun timeToLiveTimeUnit(timeToLiveTimeUnit: TimeUnit): HttpClientBuilder {
        this.timeToLiveTimeUnit = timeToLiveTimeUnit
        return this
    }

    fun disableSslValidation(disableSslValidation: Boolean): HttpClientBuilder {
        this.disableSslValidation = disableSslValidation
        return this
    }

    fun followRedirects(followRedirects: Boolean): HttpClientBuilder {
        this.followRedirects = followRedirects
        return this
    }

    fun sslProtocolVersion(sslType: String): HttpClientBuilder {
        this.sslProtocolVersion = sslType
        return this
    }

    fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): HttpClientBuilder {
        this.retryOnConnectionFailure = retryOnConnectionFailure
        return this
    }

    fun cookieStore(cookieStore: CookieStore): HttpClientBuilder {
        this.cookieStore = cookieStore
        return this
    }

    fun addInterceptor(httpInterceptor: HttpInterceptor): HttpClientBuilder {
        this.httpInterceptorList.add(httpInterceptor)
        return this
    }

    /**
     * 创建客户端实例
     */
    @Throws(HttpException::class)
    fun build(): AcpClient = OkHttpClient.Builder()
            .connectTimeout(connectTimeOut.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(readTimeOut.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeOut.toLong(), TimeUnit.MILLISECONDS)
            .followRedirects(followRedirects)
            .followSslRedirects(followRedirects)
            .connectionPool(ConnectionPool(maxTotalConn, timeToLive, timeToLiveTimeUnit))
            .retryOnConnectionFailure(retryOnConnectionFailure)
            .cookieJar(cookieStore).also {
                httpInterceptorList.forEach { httpInterceptor ->
                    run {
                        it.addInterceptor(httpInterceptor)
                    }
                }
            }.let {
                AcpClient(it, disableSslValidation, sslProtocolVersion)
            }

}