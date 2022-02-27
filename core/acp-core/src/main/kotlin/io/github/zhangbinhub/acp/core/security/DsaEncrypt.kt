package io.github.zhangbinhub.acp.core.security

import io.github.zhangbinhub.acp.core.tools.CommonUtils
import org.bouncycastle.util.encoders.Base64
import java.security.KeyFactory
import java.security.Signature
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object DsaEncrypt {

    private const val ALGORITHM = "DSA"

    private const val ENCODE = CommonUtils.defaultCharset

    /**
     * 签名
     * @param data 待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    @JvmStatic
    @Throws(Exception::class)
    fun sign(data: String, privateKey: DSAPrivateKey): String {
        val keyBytes = privateKey.encoded
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val priKey = keyFactory.generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(keyFactory.algorithm)
        signature.initSign(priKey)
        signature.update(data.toByteArray(charset(ENCODE)))
        return Base64.toBase64String(signature.sign())
    }

    /**
     * 验签
     * @param data 待验证数据
     * @param sign 签名
     * @return true|false
     */
    @JvmStatic
    @Throws(Exception::class)
    fun verify(data: String, publicKey: DSAPublicKey, sign: String): Boolean {
        val keyBytes = publicKey.encoded
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val pubKey = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance(keyFactory.algorithm)
        signature.initVerify(pubKey)
        signature.update(data.toByteArray(charset(ENCODE)))
        return signature.verify(Base64.decode(sign))
    }

}
