package pers.acp.test.kotlin.test.google

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

/**
 * @author zhangbin
 * @date 16/03/2018
 * @since JDK 11
 */
object GoogleAuthenticatorUtils {

    /**
     * 生成随机密钥
     */
    fun generateSecretKey(): String {
        val sr: SecureRandom? = SecureRandom.getInstance("SHA1PRNG")
        sr!!.setSeed(Base64.decodeBase64("g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx"))
        return String(Base32().encode(sr.generateSeed(10))).toUpperCase()
    }

    /**
     * 进行验证
     * @param secretKey 密钥
     * @param code 验证码
     * @param crypto hash算法，默认 HmacSHA1，支持：HmacSHA1、HmacSHA256、HmacSHA512
     * @param timeExcursionConfig 时间偏移量，默认 1
     * 用于防止客户端时间不精确导致生成的TOTP与服务器端的TOTP一直不一致
     * 如果为0,当前时间为 10:10:15
     * 则表明在 10:10:00-10:10:30 之间生成的TOTP 能校验通过
     * 如果为1,则表明在
     * 10:09:30-10:10:00
     * 10:10:00-10:10:30
     * 10:10:30-10:11:00 之间生成的TOTP 能校验通过
     * 以此类推
     */
    fun verify(
        secretKey: String,
        code: String,
        crypto: String = "HmacSHA1",
        timeExcursionConfig: String? = "1"
    ): Boolean {
        setupParam(crypto, timeExcursionConfig)
        val time = System.currentTimeMillis() / 1000 / 30
        for (i in -timeExcursion..timeExcursion) {
            val totp = getTOTP(secretKey, time + i)
            println("vtotp=$totp")
            if (code == totp) {
                return true
            }
        }
        return false
    }

    /**
     * 参数设置
     * @param crypto hash算法
     * @param timeExcursionConfig 时间偏移量
     */
    private fun setupParam(crypto: String, timeExcursionConfig: String?) {
        GoogleAuthenticatorUtils.crypto = crypto
        if (!timeExcursionConfig.isNullOrBlank()) {
            timeExcursion = timeExcursionConfig.toInt()
        }
    }

    /**
     * 计算动态随机码
     * @param secretKey 密钥
     * @param time 时间戳
     */
    private fun getTOTP(secretKey: String, time: Long): String =
        generateTOTP(
            Hex.encodeHexString(Base32().decode(secretKey.toUpperCase())),
            java.lang.Long.toHexString(time), 6
        )

    /**
     * 生成动态随机码
     * @param key 密钥
     * @param timeStr 时间戳
     * @parma returnDigits 随机码位数
     */
    private fun generateTOTP(key: String, timeStr: String, returnDigits: Int): String {
        var time = timeStr
        while (time.length < 16)
            time = "0$time"
        val hash = dpHash(hexStr2Bytes(key), hexStr2Bytes(time))
        val offset = (hash[hash.size - 1] and 0xf).toInt()
        val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset + 1].toInt() and 0xff) shl 16) or
                ((hash[offset + 2].toInt() and 0xff) shl 8) or
                (hash[offset + 3].toInt() and 0xff)
        val digitsPower = intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000)
        val otp = binary % digitsPower[returnDigits]
        var result = otp.toString()
        while (result.length < returnDigits) {
            result = "0$result"
        }
        return result
    }

    /**
     * 十六进制字符串转字节数组
     */
    private fun hexStr2Bytes(hex: String): ByteArray {
        val bArray = BigInteger("10$hex", 16).toByteArray()
        val ret = ByteArray(bArray.size - 1)
        for (i in ret.indices)
            ret[i] = bArray[i + 1]
        return ret
    }

    /**
     * 计算mac
     */
    private fun dpHash(keyBytes: ByteArray, text: ByteArray): ByteArray =
        Mac.getInstance(crypto).let {
            it.init(SecretKeySpec(keyBytes, "RAW"))
            it.doFinal(text)
        }

    /**
     * 时间前后偏移量
     * 用于防止客户端时间不精确导致生成的TOTP与服务器端的TOTP一直不一致
     * 如果为0,当前时间为 10:10:15
     * 则表明在 10:10:00-10:10:30 之间生成的TOTP 能校验通过
     * 如果为1,则表明在
     * 10:09:30-10:10:00
     * 10:10:00-10:10:30
     * 10:10:30-10:11:00 之间生成的TOTP 能校验通过
     * 以此类推
     */
    private var timeExcursion: Int = 1

    /**
     * 计算随机码mac算法
     * 支持：HmacSHA1、HmacSHA256、HmacSHA512
     */
    private var crypto = "HmacSHA1"

}