package pers.acp.core.task.timer.rule

import pers.acp.core.exceptions.EnumValueUndefinedException

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
                nameMap[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): CircleType {
            if (nameMap.containsKey(name.toLowerCase())) {
                return nameMap.getValue(name.toLowerCase())
            }
            throw EnumValueUndefinedException(CircleType::class.java, name)
        }
    }

}
