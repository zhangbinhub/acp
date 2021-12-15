package com.github.zhangbinhub.acp.core.security.key

import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

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
                nameMap[type.name.uppercase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): KeyType {
            if (nameMap.containsKey(name.lowercase())) {
                return nameMap.getValue(name.lowercase())
            }
            throw EnumValueUndefinedException(KeyType::class.java, name)
        }
    }

}