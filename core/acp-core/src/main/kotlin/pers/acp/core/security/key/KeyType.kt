package pers.acp.core.security.key

import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class KeyType {

    AES,
    DES,
    DESede,
    RSA,
    DSA,
    HMAC,
    RandomStr,
    RandomNumber,
    RandomChar;

    companion object {

        private var nameMap: MutableMap<String, KeyType> = mutableMapOf()

        init {
            for (type in values()) {
                nameMap[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): KeyType {
            if (nameMap.containsKey(name.toLowerCase())) {
                return nameMap.getValue(name.toLowerCase())
            }
            throw EnumValueUndefinedException(KeyType::class.java, name)
        }
    }

}