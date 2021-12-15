package com.github.zhangbinhub.acp.core.task.timer.rule

import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class CircleType {

    Time,
    Day,
    Week,
    Month,
    Quarter,
    Year;

    companion object {

        private var nameMap: MutableMap<String, CircleType> = mutableMapOf()

        init {
            for (type in values()) {
                nameMap[type.name.uppercase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): CircleType {
            if (nameMap.containsKey(name.lowercase())) {
                return nameMap.getValue(name.lowercase())
            }
            throw EnumValueUndefinedException(CircleType::class.java, name)
        }
    }

}
