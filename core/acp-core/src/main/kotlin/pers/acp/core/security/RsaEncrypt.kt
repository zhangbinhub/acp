package pers.acp.core.security

import pers.acp.core.tools.CommonUtils
import org.bouncycastle.util.encoders.Base64

import javax.crypto.Cipher
import java.security.Key
import java.security.interfaces.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object RsaEncrypt {

    private const val CRYPT_TYPE = "RSA/ECB/PKCS1Padding"

    private const val ENCODE = CommonUtils.defaultCharset

    /**
     * 公钥加密
     *
     * @param data       待加密字符串
     * @param publicKey  公钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun encryptByPublicKey(data: String, publicKey: RSAPublicKey, cryptType: String = CRYPT_TYPE): String {
        return doEncrypt(data, publicKey, cryptType)
    }

    /**
     * 私钥解密
     *
     * @param data       加密字符串
     * @param privateKey 私钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun decryptByPrivateKey(data: String, privateKey: RSAPrivateKey, cryptType: String = CRYPT_TYPE): String {
        return doDecrypt(data, privateKey, cryptType)
    }

    /**
     * 私钥加密
     *
     * @param data       待加密字符串
     * @param privateKey 私钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 密文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun encryptByPrivateKey(data: String, privateKey: RSAPrivateKey, cryptType: String = CRYPT_TYPE): String {
        return doEncrypt(data, privateKey, cryptType)
    }

    /**
     * 公钥解密
     *
     * @param data       加密字符串
     * @param publicKey  公钥
     * @param cryptType 加密类型，默认 RSA/ECB/PKCS1Padding
     * @return 明文
     */
    @JvmStatic
    @Throws(Exception::class)
    @JvmOverloads
    fun decryptByPublicKey(data: String, publicKey: RSAPublicKey, cryptType: String = CRYPT_TYPE): String {
        return doDecrypt(data, publicKey, cryptType)
    }

    @Throws(Exception::class)
    private fun doEncrypt(data: String, key: RSAKey, cryptType: String = CRYPT_TYPE): String {
        val cipher = Cipher.getInstance(cryptType)
        cipher.init(Cipher.ENCRYPT_MODE, key as Key)
        // 模长
        val keyLen = key.modulus.bitLength() / 8
        // 加密数据长度 <= 模长-11
        val dataList = splitString(data, keyLen - 11)
        val mi = StringBuilder()
        // 如果明文长度大于模长-11则要分组加密
        for (s in dataList) {
            s?.let {
                mi.append(Base64.toBase64String(cipher.doFinal(it.toByteArray(charset(ENCODE)))))
            }
        }
        return mi.toString()
    }

    @Throws(Exception::class)
    private fun doDecrypt(data: String, key: RSAKey, cryptType: String = CRYPT_TYPE): String {
        val cipher = Cipher.getInstance(cryptType)
        cipher.init(Cipher.DECRYPT_MODE, key as Key)
        // 模长
        val keyLen = key.modulus.bitLength() / 8
        val bytes = data.toByteArray()
        val bcd = Base64.decode(bytes)
        // 如果密文长度大于模长则要分组解密
        val ming = StringBuilder()
        val arrays = splitArray(bcd, keyLen)
        for (arr in arrays) {
            arr?.let {
                ming.append(String(cipher.doFinal(it), charset(ENCODE)))
            }
        }
        return ming.toString()
    }

    /**
     * 拆分字符串
     */
    private fun splitString(string: String, len: Int): Array<String?> {
        val x = string.length / len
        val y = string.length % len
        var z = 0
        if (y != 0) {
            z = 1
        }
        val strings = arrayOfNulls<String>(x + z)
        for (i in 0 until x + z) {
            strings[i] = if (i == x + z - 1 && y != 0) {
                string.substring(i * len, i * len + y)
            } else {
                string.substring(i * len, i * len + len)
            }
        }
        return strings
    }

    /**
     * 拆分数组
     */
    private fun splitArray(data: ByteArray, len: Int): Array<ByteArray?> {
        val x = data.size / len
        val y = data.size % len
        var z = 0
        if (y != 0) {
            z = 1
        }
        val arrays = arrayOfNulls<ByteArray>(x + z)
        var arr: ByteArray
        for (i in 0 until x + z) {
            arr = ByteArray(len)
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y)
            } else {
                System.arraycopy(data, i * len, arr, 0, len)
            }
            arrays[i] = arr
        }
        return arrays
    }
}