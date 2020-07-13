package pers.acp.kotlin.test.google

import java.net.URLEncoder

/**
 * @author zhangbin
 * @date 16/03/2018
 * @since JDK 11
 */
fun main() {
    generateQRcode()
//    val code = "061001"
//    println("code=$code")
//    println(GoogleAuthenticatorUtils.verify(
//            secretKey = "P5JVA7W6QVLA5SEM",
//            code = code,
//            timeExcursionConfig = "1"))
}

fun generateQRcode() {
    val secretKey = GoogleAuthenticatorUtils.generateSecretKey()
    val qrCodeData = "otpauth://totp/%s?secret=%s"
    val qrCodeDataInfo = String.format(qrCodeData,
            URLEncoder.encode("Xstar:zhangbin", "UTF-8").replace("+", "%20"),
            URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20"))
    println("usr=" + "Xstar:zhangbin")
    println("secretKey=$secretKey")
    println("qrCodeDataInfo=$qrCodeDataInfo")
}