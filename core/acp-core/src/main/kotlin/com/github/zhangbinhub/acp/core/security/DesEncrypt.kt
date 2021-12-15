package com.github.zhangbinhub.acp.core.security

import org.bouncycastle.util.encoders.Base64
import com.github.zhangbinhub.acp.core.tools.CommonUtils

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import java.security.Key

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object DesEncrypt {

    private val ZERO_IVC = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

    private const val ENCODE = CommonUtils.defaultCharset

    private const val CRYPT_TYPE_3DES_CBC = "DESede/CBC/PKCS5Padding"

    private const val CRYPT_TYPE_3DES_ECB = "DESede/ECB/PKCS5Padding"

    private const val CRYPT_TYPE_DES_CBC = "DES/CBC/PKCS5Padding"

    private const val CRYPT_TYPE_DES_ECB = "DES/ECB/PKCS5Padding"

    /**
     * 3DES加密cbc模式 DESede/CBC/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encryptBy3DesCbc(plainText: String, key: Key): String = encrypt(plainText, key, CRYPT_TYPE_3DES_CBC)

    /**
     * 3DES解密cbc模式 DESede/CBC/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decryptBy3DesCbc(encryptedText: String, key: Key): String = decrypt(encryptedText, key, CRYPT_TYPE_3DES_CBC)

    /**
     * 3DES加密ecb模式 DESede/ECB/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encryptBy3DesEcb(plainText: String, key: Key): String = encrypt(plainText, key, CRYPT_TYPE_3DES_ECB)

    /**
     * 3DES解密ecb模式 DESede/ECB/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decryptBy3DesEcb(encryptedText: String, key: Key): String = decrypt(encryptedText, key, CRYPT_TYPE_3DES_ECB)

    /**
     * DES加密cbc模式 DES/CBC/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encryptByDesCbc(plainText: String, key: Key): String = encrypt(plainText, key, CRYPT_TYPE_DES_CBC)

    /**
     * DES解密cbc模式 DES/CBC/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decryptByDesCbc(encryptedText: String, key: Key): String = decrypt(encryptedText, key, CRYPT_TYPE_DES_CBC)

    /**
     * DES加密ecb模式 DES/ECB/PKCS5Padding
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @return 加密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encryptByDesEcb(plainText: String, key: Key): String = encrypt(plainText, key, CRYPT_TYPE_DES_ECB)

    /**
     * DES解密ecb模式 DES/ECB/PKCS5Padding
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @return 解密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decryptByDesEcb(encryptedText: String, key: Key): String = decrypt(encryptedText, key, CRYPT_TYPE_DES_ECB)

    /**
     * 加密
     *
     * @param plainText 待加密数据
     * @param key       秘钥
     * @param cryptType 加密类型，默认 DESede/ECB/PKCS5Padding
     * @return 加密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encrypt(plainText: String, key: Key, cryptType: String = CRYPT_TYPE_3DES_ECB): String {
        val cipher = Cipher.getInstance(cryptType)
        if (cryptType.uppercase().contains("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } else {
            val iv = IvParameterSpec(ZERO_IVC)
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        }
        return Base64.toBase64String(cipher.doFinal(plainText.toByteArray(charset(ENCODE)))).trim()
    }

    /**
     * 解密
     *
     * @param encryptedText 待解密数据
     * @param key           秘钥
     * @param cryptType     加密类型，默认 DESede/ECB/PKCS5Padding
     * @return 解密结果
     */
    @JvmStatic
    @Throws(Exception::class)
    fun decrypt(encryptedText: String, key: Key, cryptType: String = CRYPT_TYPE_3DES_ECB): String {
        val cipher = Cipher.getInstance(cryptType)
        if (cryptType.uppercase().contains("ECB")) {
            cipher.init(Cipher.DECRYPT_MODE, key)
        } else {
            val iv = IvParameterSpec(ZERO_IVC)
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
        }
        return String(cipher.doFinal(Base64.decode(encryptedText)), charset(ENCODE)).trim()
    }

}
