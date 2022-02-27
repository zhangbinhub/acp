package io.github.zhangbinhub.acp.core.match

import io.github.zhangbinhub.acp.core.exceptions.OperateException
import io.github.zhangbinhub.acp.core.tools.CommonUtils
import java.util.*
import kotlin.math.pow

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
class Operate {

    private val priStack = LinkedList<Char>()

    private val numStack = LinkedList<Double>()

    /**
     * 进行四则运算
     *
     * @param str 运算表达式
     * @return 运算结果:double
     */
    @Throws(OperateException::class)
    fun calculate(str: String): Double {
        var temp: String
        val tempNum = StringBuilder()
        val string = StringBuilder().append(str).append("#")

        while (string.isNotEmpty()) {
            temp = string.substring(0, 1)
            string.delete(0, 1)
            if (!isNum(temp)) {
                if (!CommonUtils.isNullStr(tempNum.toString())) {
                    val num = java.lang.Double.parseDouble(tempNum.toString())
                    numStack.push(num)
                    tempNum.delete(0, tempNum.length)
                }
                while (!compare(temp[0]) && !priStack.isEmpty()) {
                    val a = numStack.pop()
                    val b: Double = if (numStack.isEmpty()) {
                        0.0
                    } else {
                        numStack.pop()
                    }
                    val ope = priStack.pop()
                    val result: Double
                    when (ope) {
                        '+' -> {
                            result = b + a
                            numStack.push(result)
                        }
                        '-' -> {
                            result = b - a
                            numStack.push(result)
                        }
                        '*' -> {
                            result = b * a
                            numStack.push(result)
                        }
                        '/' -> {
                            result = b / a
                            if (java.lang.Double.isInfinite(result) || java.lang.Double.isNaN(result)) {
                                throw OperateException("in division,the divisor is Illegal")
                            }
                            numStack.push(result)
                        }
                        '^' -> {
                            result = b.pow(a)
                            numStack.push(result)
                        }
                    }
                }
                if (temp[0] != '#') {
                    priStack.push(temp[0])
                    if (temp[0] == ')') {
                        priStack.pop()
                        priStack.pop()
                    }
                }
            } else {
                tempNum.append(temp)
            }
        }
        return numStack.pop()
    }

    /**
     * 判断传入的字符是否合法
     *
     * @param temp 传入的字符串
     * @return 是否合法
     */
    private fun isNum(temp: String): Boolean {
        return temp.matches("[0-9]|\\.".toRegex())
    }

    /**
     * 比较当前操作符与栈顶元素操作符优先级,如果比栈顶元素优先级高,则返回true,否则返回false
     *
     * @param str 需要进行比较的字符
     * @return 比较结果 true代表比栈顶元素优先级高,false代表比栈顶元素优先级低
     */
    private fun compare(str: Char): Boolean {
        if (priStack.isEmpty()) {
            return true
        }
        val last = priStack.peek()
        if (last == '(') {
            return true
        }
        when (str) {
            '#' -> return false
            '(' -> return true
            ')' -> return false
            '^' -> return last == '*' || last == '/' || last == '+' || last == '-'
            '*' -> return last == '+' || last == '-'
            '/' -> return last == '+' || last == '-'
            '+' -> return false
            '-' -> return false
        }
        return true
    }
}
