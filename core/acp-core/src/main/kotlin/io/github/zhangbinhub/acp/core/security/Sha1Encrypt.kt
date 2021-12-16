package io.github.zhangbinhub.acp.core.security

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object Sha1Encrypt {

    /**
     * 加密
     *
     * @param plainText 待加密字符串
     * @return 密文
     */
    @JvmStatic
    fun encrypt(plainText: String): String = SignatureEncrypt.encrypt(plainText, SignatureEncrypt.SHA1)

}