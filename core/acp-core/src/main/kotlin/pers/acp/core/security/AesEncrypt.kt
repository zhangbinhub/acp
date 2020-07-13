package pers.acp.core.security

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
import pers.acp.core.tools.CommonUtils
import org.bouncycastle.util.encoders.Base64

import javax.crypto.Cipher
import java.security.Key

object AesEncrypt {

    private const val CRYPT_TYPE = "AES/ECB/PKCS5Padding"

    private const val ENCODE = CommonUtils.defaultCharset

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @param key       密钥
     * @param cryptType 加密类型，默认 AES/ECB/PKCS5Padding
     * @return 密文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun encrypt(plainText: String, key: Key, cryptType: String = CRYPT_TYPE): String {
        val cipher = Cipher.getInstance(cryptType)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.toBase64String(cipher.doFinal(plainText.toByteArray(charset(ENCODE)))).trim()
    }

    /**
     * 解密
     *
     * @param encryptedText 加密字符串
     * @param key           密钥
     * @param cryptType     加密类型，默认 AES/ECB/PKCS5Padding
     * @return 明文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun decrypt(encryptedText: String, key: Key, cryptType: String = CRYPT_TYPE): String {
        val cipher = Cipher.getInstance(cryptType)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.decode(encryptedText)), charset(ENCODE)).trim()
    }

}