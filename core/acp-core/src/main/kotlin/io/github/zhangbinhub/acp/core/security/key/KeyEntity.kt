package io.github.zhangbinhub.acp.core.security.key

import io.github.zhangbinhub.acp.core.security.HmacEncrypt
import io.github.zhangbinhub.acp.core.tools.CommonUtils
import java.io.Serializable
import java.security.Key
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
data class KeyEntity(
        val keyType: KeyType,
        var traitId: String,
        var key: Key? = null,
        var rsaPublicKey: RSAPublicKey? = null,
        var rsaPrivateKey: RSAPrivateKey? = null,
        var dsaPublicKey: DSAPublicKey? = null,
        var dsaPrivateKey: DSAPrivateKey? = null,
        var randomString: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = -7223413771603126570L

        /**
         * 生成密钥实体
         *
         * @param keyType   密钥类型
         * @param cryptType 加密算法
         * @param traitId   申请者身份标识字符串
         * @param length    密钥长度（随机字符串密钥时有效）
         * @return 密钥实体
         */
        @Throws(Exception::class)
        internal fun generateEntity(keyType: KeyType, cryptType: String = HmacEncrypt.CRYPT_TYPE, traitId: String, length: Int = 0): KeyEntity {
            val entity = KeyEntity(keyType, traitId)
            when (keyType) {
                KeyType.AES -> {
                    val aesKeyStr = CommonUtils.getUuid16()
                    entity.key = KeyManagement.getAESKey(aesKeyStr)
                }
                KeyType.DES -> {
                    val desKeyStr = CommonUtils.getUuid8()
                    entity.key = KeyManagement.getDESKey(desKeyStr)
                }
                KeyType.DESede -> {
                    val desedeKeyStr = CommonUtils.getUuid24()
                    entity.key = KeyManagement.get3DESKey(desedeKeyStr)
                }
                KeyType.HMAC -> {
                    if (length <= 0) {
                        throw SecurityException("the length must be greater than 0")
                    }
                    val keyStr = KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length)
                    entity.key = KeyManagement.getKey(keyStr, cryptType)
                }
                KeyType.RSA -> {
                    val rsaKeys = KeyManagement.getRsaKeys()
                    entity.rsaPublicKey = rsaKeys[0] as RSAPublicKey
                    entity.rsaPrivateKey = rsaKeys[1] as RSAPrivateKey
                }
                KeyType.DSA -> {
                    val dsaKeys = KeyManagement.getDsaKeys()
                    entity.dsaPublicKey = dsaKeys[0] as DSAPublicKey
                    entity.dsaPrivateKey = dsaKeys[1] as DSAPrivateKey
                }
                KeyType.RandomStr -> {
                    if (length <= 0) {
                        throw SecurityException("the length must be greater than 0")
                    }
                    entity.randomString = KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length)
                }
                KeyType.RandomNumber -> {
                    if (length <= 0) {
                        throw SecurityException("the length must be greater than 0")
                    }
                    entity.randomString = KeyManagement.getRandomString(KeyManagement.RANDOM_NUMBER, length)
                }
                KeyType.RandomChar -> {
                    if (length <= 0) {
                        throw SecurityException("the length must be greater than 0")
                    }
                    entity.randomString = KeyManagement.getRandomString(KeyManagement.RANDOM_CHAR, length)
                }
            }
            return entity
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyEntity

        if (keyType != other.keyType) return false
        if (traitId != other.traitId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + traitId.hashCode()
        return result
    }

}