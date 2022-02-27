package io.github.zhangbinhub.acp.core.client.http

import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.client.exceptions.HttpException
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.packet.http.HttpPacket
import okhttp3.*
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
class AcpClient
@Throws(HttpException::class)
constructor(val builder: OkHttpClient.Builder, disableSslValidation: Boolean, sslType: String) {

    private val log = LogFactory.getInstance(this.javaClass)

    private var client: OkHttpClient? = null

    init {
        if (disableSslValidation) {
            try {
                val disabledTrustManager = object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                }
                val trustManagers = arrayOfNulls<TrustManager>(1)
                trustManagers[0] = disabledTrustManager
                val sslContext = SSLContext.getInstance(sslType)
                sslContext.init(null, trustManagers, java.security.SecureRandom())
                val disabledSSLSocketFactory = sslContext.socketFactory
                this.builder.sslSocketFactory(disabledSSLSocketFactory, disabledTrustManager)
                this.builder.hostnameVerifier { _, _ -> true }
            } catch (e: NoSuchAlgorithmException) {
                log.error(e.message, e)
                throw HttpException("Error setting SSLSocketFactory in OKHttpClient: " + e.message)
            } catch (e: KeyManagementException) {
                log.error(e.message, e)
                throw HttpException("Error setting SSLSocketFactory in OKHttpClient: " + e.message)
            }

        }
    }

    private fun basicAuth(requestParam: RequestParam) {
        if (!CommonTools.isNullStr(requestParam.basicUsername) && !CommonTools.isNullStr(requestParam.basicPassword)) {
            this.builder.authenticator { _, response ->
                val credential = Credentials.basic(requestParam.basicUsername!!, requestParam.basicPassword!!)
                response.request().newBuilder().header("Authorization", credential).build()
            }
        }
    }

    /**
     * GET请求
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doGet(requestParam: RequestParam): ResponseResult =
        doRequest(
            requestParam,
            Request.Builder()
                .url(HttpPacket.buildGetParam(requestParam.url, requestParam.params, requestParam.clientCharset))
        )

    /**
     * GET请求
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doGetAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) =
        doRequestAsync(
            requestParam,
            Request.Builder()
                .url(HttpPacket.buildGetParam(requestParam.url, requestParam.params, requestParam.clientCharset)),
            httpCallBack
        )

    /**
     * POST请求
     * 键值对
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doPost(requestParam: RequestParam): ResponseResult {
        val body = FormBody.Builder()
        return doRequest(requestParam.also {
            it.params.forEach { (name, value) -> body.add(name, value) }
        }, Request.Builder().url(requestParam.url).post(body.build()))
    }

    /**
     * POST请求
     * 键值对
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doPostAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) {
        val body = FormBody.Builder()
        doRequestAsync(requestParam.also {
            it.params.forEach { (name, value) -> body.add(name, value) }
        }, Request.Builder().url(requestParam.url).post(body.build()), httpCallBack)
    }

    /**
     * POST请求发送XML
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doPostXml(requestParam: RequestParam): ResponseResult {
        val body = RequestBody.create(
            MediaType.parse("application/xml;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        return doRequest(requestParam, Request.Builder().url(requestParam.url).post(body))
    }

    /**
     * POST请求发送XML
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doPostXmlAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) {
        val body = RequestBody.create(
            MediaType.parse("application/xml;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        doRequestAsync(requestParam, Request.Builder().url(requestParam.url).post(body), httpCallBack)
    }

    /**
     * POST请求发送JSON字符串
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doPostJson(requestParam: RequestParam): ResponseResult {
        val body = RequestBody.create(
            MediaType.parse("application/json;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        return doRequest(requestParam, Request.Builder().url(requestParam.url).post(body))
    }

    /**
     * POST请求发送JSON字符串
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doPostJsonAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) {
        val body = RequestBody.create(
            MediaType.parse("application/json;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        doRequestAsync(requestParam, Request.Builder().url(requestParam.url).post(body), httpCallBack)
    }

    /**
     * POST请求发送SOAP报文数据
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doPostSoap(requestParam: RequestParam): ResponseResult {
        val body = RequestBody.create(
            MediaType.parse("application/soap+xml;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        return doRequest(requestParam, Request.Builder().url(requestParam.url).post(body))
    }

    /**
     * POST请求发送SOAP报文数据
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doPostSoapAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) {
        val body = RequestBody.create(
            MediaType.parse("application/soap+xml;charset=" + requestParam.clientCharset),
            requestParam.bodyString
        )
        doRequestAsync(requestParam, Request.Builder().url(requestParam.url).post(body), httpCallBack)
    }

    /**
     * POST请求发送字节数组数据
     *
     * @param requestParam 请求参数
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doPostBytes(requestParam: RequestParam): ResponseResult {
        if (requestParam.bodyBytes == null) {
            throw HttpException("the bodyBytes is null")
        }
        val body = RequestBody.create(
            MediaType.parse("application/octet-stream"),
            requestParam.bodyBytes!!
        )
        return doRequest(requestParam, Request.Builder().url(requestParam.url).post(body))
    }

    /**
     * POST请求发送字节数组数据
     *
     * @param requestParam 请求参数
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doPostBytesAsync(requestParam: RequestParam, httpCallBack: HttpCallBack) {
        if (requestParam.bodyBytes == null) {
            throw HttpException("the bodyBytes is null")
        }
        val body = RequestBody.create(
            MediaType.parse("application/octet-stream"),
            requestParam.bodyBytes!!
        )
        doRequestAsync(requestParam, Request.Builder().url(requestParam.url).post(body), httpCallBack)
    }

    /**
     * 自定义请求
     *
     * @param method       请求类型
     * @param requestParam 请求参数（body不能为空）
     * @return 响应信息
     */
    @Throws(HttpException::class)
    fun doRequest(method: String, requestParam: RequestParam): ResponseResult {
        if (requestParam.body == null) {
            throw HttpException("the body is null")
        }
        return doRequest(requestParam, Request.Builder().url(requestParam.url).method(method, requestParam.body))
    }

    /**
     * 自定义请求
     *
     * @param method       请求类型
     * @param requestParam 请求参数（body不能为空）
     * @param httpCallBack 回调对象
     */
    @Throws(HttpException::class)
    fun doRequestAsync(method: String, requestParam: RequestParam, httpCallBack: HttpCallBack) {
        if (requestParam.body == null) {
            throw HttpException("the body is null")
        }
        doRequestAsync(
            requestParam,
            Request.Builder().url(requestParam.url).method(method.uppercase(), requestParam.body),
            httpCallBack
        )
    }

    /**
     * 执行同步请求
     *
     * @param requestParam   请求参数
     * @param requestBuilder 请求对象构造器
     * @return 响应
     */
    @Throws(HttpException::class)
    private fun doRequest(requestParam: RequestParam, requestBuilder: Request.Builder): ResponseResult {
        try {
            if (client == null) {
                basicAuth(requestParam)
                client = builder.build()
            }
            return parseResponseResult(client!!.newCall(requestBuilder.also {
                requestParam.requestHeaders.forEach { (name, value) -> it.addHeader(name, value) }
            }.build()).execute())
        } catch (e: Exception) {
            log.error(e.message, e)
            throw HttpException(e.message)
        }

    }

    /**
     * 执行异步请求
     *
     * @param requestParam   请求参数
     * @param requestBuilder 请求对象构造器
     * @param httpCallBack   回调对象
     */
    @Throws(HttpException::class)
    private fun doRequestAsync(
        requestParam: RequestParam,
        requestBuilder: Request.Builder,
        httpCallBack: HttpCallBack
    ) {
        try {
            if (client == null) {
                basicAuth(requestParam)
                client = builder.build()
            }
            client!!.newCall(requestBuilder.also {
                requestParam.requestHeaders.forEach { (name, value) -> it.addHeader(name, value) }
            }.build()).enqueue(httpCallBack)
        } catch (e: Exception) {
            log.error(e.message, e)
            throw HttpException(e.message)
        }

    }

    companion object {
        /**
         * 处理响应
         *
         * @param response 响应对象
         * @return 转换后的响应对象
         * @throws IOException 异常
         */
        @Throws(IOException::class)
        internal fun parseResponseResult(response: Response): ResponseResult =
            ResponseResultBuilder(response)
                .headers(response.headers())
                .status(response.code()).also {
                    response.body()?.apply {
                        it.bodyString(this.string())
                    }
                }.build()
    }

}