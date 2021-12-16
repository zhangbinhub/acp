package io.github.zhangbinhub.acp.core.match

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object MoneyToCN {

    /**
     * 汉语中数字大写
     */
    private val CN_UPPER_NUMBER = arrayOf("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖")

    /**
     * 汉语中货币单位大写，这样的设计类似于占位符
     */
    private val CN_UPPER_CN = arrayOf("分", "角", "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟")

    /**
     * 特殊字符：整
     */
    private const val CN_FULL = "整"

    /**
     * 特殊字符：负
     */
    private const val CN_NEGATIVE = "负"

    /**
     * 金额的精度，默认值为2
     */
    private const val MONEY_PRECISION = 2

    /**
     * 特殊字符：零元整
     */
    private const val CN_ZERO_FULL = "零元$CN_FULL"

    /**
     * 把输入的金额转换为汉语中人民币的大写
     *
     * @param money 输入的金额
     * @return 对应的汉语大写
     */
    @JvmStatic
    fun moneyToCn(money: Double): String {
        val numberOfMoney = BigDecimal(money)
        val sb = StringBuilder()
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        val sigNum = numberOfMoney.signum()
        // 零元整的情况
        if (sigNum == 0) {
            return CN_ZERO_FULL
        }
        // 这里会进行金额的四舍五入
        var number = numberOfMoney.movePointRight(MONEY_PRECISION)
                .setScale(0, RoundingMode.HALF_UP).abs().toLong()
        // 得到小数点后两位值
        val scale = number % 100
        var numUnit: Int
        var numIndex = 0
        var getZero = false
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (scale <= 0) {
            numIndex = 2
            number /= 100
            getZero = true
        }
        if (scale > 0 && scale % 10 <= 0) {
            numIndex = 1
            number /= 10
            getZero = true
        }
        var zeroSize = 0
        while (number > 0) {
            // 每次获取到最后一个数
            numUnit = (number % 10).toInt()
            if (numUnit > 0) {
                if (numIndex == 9 && zeroSize >= 3) {
                    sb.insert(0, CN_UPPER_CN[6])
                }
                if (numIndex == 13 && zeroSize >= 3) {
                    sb.insert(0, CN_UPPER_CN[10])
                }
                sb.insert(0, CN_UPPER_CN[numIndex])
                sb.insert(0, CN_UPPER_NUMBER[numUnit])
                getZero = false
                zeroSize = 0
            } else {
                ++zeroSize
                if (!getZero) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit])
                }
                if (numIndex == 2) {
                    sb.insert(0, CN_UPPER_CN[numIndex])
                } else if ((numIndex - 2) % 4 == 0 && number % 1000 > 0) {
                    sb.insert(0, CN_UPPER_CN[numIndex])
                }
                getZero = true
            }
            // 让number每次都去掉最后一个数
            number /= 10
            ++numIndex
        }
        // 如果sigNum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (sigNum == -1) {
            sb.insert(0, CN_NEGATIVE)
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (scale <= 0) {
            sb.append(CN_FULL)
        }
        return sb.toString()
    }
}
