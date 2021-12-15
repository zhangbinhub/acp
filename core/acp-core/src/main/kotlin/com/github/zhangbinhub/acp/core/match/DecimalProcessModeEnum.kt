package com.github.zhangbinhub.acp.core.match

import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException
import java.math.RoundingMode

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
enum class DecimalProcessModeEnum(val mode: RoundingMode, val description: String) {

    Up(RoundingMode.UP, "入"),

    Down(RoundingMode.DOWN, "舍"),

    Ceiling(RoundingMode.CEILING, "正无穷大"),

    Floor(RoundingMode.FLOOR, "负无穷大"),

    HalfUp(RoundingMode.HALF_UP, "四舍五入"),

    HalfDown(RoundingMode.HALF_DOWN, "五舍六入"),

    HalfEven(RoundingMode.HALF_EVEN, "银行家舍入（左为奇，四舍五入；左为偶，五舍六入）");

    companion object {

        private var nameMap: MutableMap<String, DecimalProcessModeEnum> = mutableMapOf()

        init {
            for (type in values()) {
                nameMap[type.name.uppercase()] = type
            }
        }

        @JvmStatic
        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): DecimalProcessModeEnum {
            if (nameMap.containsKey(name.lowercase())) {
                return nameMap.getValue(name.lowercase())
            }
            throw EnumValueUndefinedException(DecimalProcessModeEnum::class.java, name)
        }
    }

}