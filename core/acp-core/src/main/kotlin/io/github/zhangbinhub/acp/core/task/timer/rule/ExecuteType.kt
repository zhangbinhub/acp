package io.github.zhangbinhub.acp.core.task.timer.rule

import io.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException

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
                nameMap[type.name.uppercase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): ExecuteType {
            if (nameMap.containsKey(name.lowercase())) {
                return nameMap.getValue(name.lowercase())
            }
            throw EnumValueUndefinedException(ExecuteType::class.java, name)
        }
    }

}