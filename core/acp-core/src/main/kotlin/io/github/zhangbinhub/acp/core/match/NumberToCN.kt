package io.github.zhangbinhub.acp.core.match

import io.github.zhangbinhub.acp.core.tools.CommonUtils
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object NumberToCN {

    /**
     * 汉语中数字大写
     */
    private val CN_UPPER_NUMBER = arrayOf("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖")

    /**
     * 汉语中单位大写，这样的设计类似于占位符
     */
    private val CN_UPPER_CN = arrayOf("", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟")

    /**
     * 特殊字符：负
     */
    private const val CN_NEGATIVE = "负"

    /**
     * 0的字符
     */
    private const val CN_ZERO_FULL = "零"

    /**
     * 把输入的金额转换为汉语大写
     *
     * @param number 输入的数字
     * @param precision 保留小数位数，默认2
     * @return 对应的汉语大写
     */
    @JvmStatic
    @JvmOverloads
    fun numberToCn(number: Double, precision: Int = 2): String {
        val bigDecimal = BigDecimal(number)
        val sb = StringBuilder()
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        val sigNum = bigDecimal.signum()
        // 零的情况
        if (sigNum == 0) {
            return CN_ZERO_FULL
        }
        // 这里会进行四舍五入
        var numberLong = bigDecimal.movePointRight(precision)
                .setScale(0, RoundingMode.HALF_UP).abs().toLong()
        // 得到小数点后的值
        val scale = numberLong % 10.toDouble().pow(precision).toLong()
        numberLong /= 10.toDouble().pow(precision).toLong()
        var numUnit: Int
        var numIndex = 0
        var getZero = true
        var zeroSize = 0
        // 转整数
        while (numberLong > 0) {
            // 每次获取到最后一个数
            numUnit = (numberLong % 10).toInt()
            if (numIndex % 4 == 0 && numIndex != 0) {
                sb.insert(0, CN_UPPER_CN[numIndex])
            }
            if (numUnit > 0) {
                if (numIndex % 4 != 0) {
                    sb.insert(0, CN_UPPER_CN[numIndex])
                }
                sb.insert(0, CN_UPPER_NUMBER[numUnit])
                getZero = false
                zeroSize = 0
            } else {
                ++zeroSize
                if (!getZero) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit])
                }
                getZero = true
            }
            // 让number每次都去掉最后一个数
            numberLong /= 10
            ++numIndex
        }
        // 转小数
        if (scale > 0) {
            sb.append("点")
            CommonUtils.strFillIn(scale.toString(), precision, 0, "0").toCharArray().forEach {
                sb.append(CN_UPPER_NUMBER[it.toString().toInt()])
            }
        }
        // 如果sigNum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (sigNum == -1) {
            sb.insert(0, CN_NEGATIVE)
        }
        return sb.toString().replace(Regex("$CN_ZERO_FULL+$"), "")
    }
}
