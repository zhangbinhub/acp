package pers.acp.core.security

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import pers.acp.core.tools.CommonUtils

import javax.crypto.Mac
import java.security.Key

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object HmacEncrypt {

    /**
     * 加密算法
     * MAC算法可选以下多种算法
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     */
    const val CRYPT_TYPE = "HmacSHA256"

    private const val ENCODE = CommonUtils.defaultCharset

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @param cryptType 加密类型，默认 HmacSHA256
     * @return 明文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun encrypt(plainText: String, key: Key, cryptType: String = CRYPT_TYPE): String {
        val mac = Mac.getInstance(cryptType)
        mac.init(key)
        return ByteUtils.toHexString(mac.doFinal(plainText.toByteArray(charset(ENCODE)))).trim()
    }

}