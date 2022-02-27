package pers.acp.test.kotlin.test.sms

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * @author zhangbin
 * @date 16/03/2018
 * @since JDK 11
 */
object SMSUtils {

    /**
     * 字符编码
     */
    private const val charsetName = "utf8"

    /**
     * 发送短信
     * @param url 请求地址
     * @param userid 账号
     * @param password 密码
     * @param phone 手机号，多个手机用“,”隔开
     * @param content 短信内容
     * @return String 返回信息
     * 提交成功，返回当前提交数据的唯一标记ID
     * 提交失败，返回错误码：
     * -2 	参数错误
     * -3	账号密码错误
     * -4	余额不足
     * -5	参数格式错误
     * -6	内容超长
     * -8	扩展长度错误
     * -9	IP错误
     * -10	时间格式错误
     * -11	mid格式错误,最大支持18位纯数字
     * -990	未知异常
     */
    fun sendSMSForHengTong(url: String, userid: String, password: String, phone: String, content: String): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val param = LinkedMultiValueMap<String, String>()
        param.setAll(
            mapOf(
                "userid" to userid,
                "password" to md5(password),
                "phone" to phone,
                "codetype" to charsetName,
                "message" to Base64.encodeBase64String(content.toByteArray(Charset.forName(charsetName))),
                "encodeway" to "base64"
            )
        )
        return RestTemplate().postForObject(url, HttpEntity(param, headers), String::class.java)
    }

    private fun md5(text: String): String =
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val byteArray = text.toByteArray(Charset.forName(charsetName))
            val md5Bytes = md5.digest(byteArray)
            Hex.encodeHexString(md5Bytes).toUpperCase()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

}