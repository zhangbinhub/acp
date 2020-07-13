package pers.acp.core.task.timer.rule

import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class ExecuteType {

    WeekDay,
    Weekend,
    All;

    companion object {

        private var nameMap: MutableMap<String, ExecuteType> = mutableMapOf()

        init {
            for (type in values()) {
                nameMap[type.name.toUpperCase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): ExecuteType {
            if (nameMap.containsKey(name.toLowerCase())) {
                return nameMap.getValue(name.toLowerCase())
            }
            throw EnumValueUndefinedException(ExecuteType::class.java, name)
        }
    }

}