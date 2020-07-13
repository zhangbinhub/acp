package pers.acp.kotlin.test.sms

import org.apache.commons.codec.binary.Base64
import java.nio.charset.Charset

/**
 * @author zhangbin
 * @date 16/03/2018
 * @since JDK 11
 */
fun main(args: Array<String>) {
//    testBase64()
//    sendSMS()
}

fun testBase64() {
    val ss = "【Xstar】你的短信验证码为 401871，有效期10分钟，请勿告诉他人。"
    val base64 = Base64.encodeBase64String(ss.toByteArray(Charset.forName("utf8")))
    println("base64 encode = $base64")
    println("base64 decode = " + String(Base64.decodeBase64(base64)))
}

fun sendSMS() {
    val response = SMSUtils.sendSMSForHengTong(
            "http://api.hengtongexun.com:8090/protocol/sendMessage.do",
            "101087",
            "Walbd0307",
            "18206778042",
            "【Xstar】你的短信验证码为 401871，有效期10分钟，请勿告诉他人。"
    )
    println(response)
}