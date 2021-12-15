package com.kernel.kotlintest

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import com.github.zhangbinhub.acp.core.CommonTools
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Create by zhangbin on 2017-12-20 11:36
 */
class TestSimple {

    @Test
    fun main() {
        val dataBean1 = DataBean1(
                "你好123", 1, 0.34
        )
        val ss = CommonTools.objectToJson(dataBean1)
        println(ss.toString())
        val dataBean1_1 = CommonTools.jsonToObject(ss, DataBean1::class.java)
        println(dataBean1_1)

//        val fold = "D:\\个人\\测试ftp"
//        val files = File(fold).listFiles()?.map { file -> file.canonicalPath } ?: listOf()
//        CommonTools.filesToZip(files, "$fold\\testzip1.zip")
//        CommonTools.filesToZip(files, "$fold\\testzip2.zip", password = "123")
//        CommonTools.zipToFiles("$fold\\testzip1.zip", "$fold\\unzip1")
//        CommonTools.zipToFiles("$fold\\testzip2.zip", "$fold\\unzip2", password = "123")
    }

    @Test
    fun testDownLoad() {
        val path = CommonTools.getWebRootAbsPath() + "/files/tmp/download".replace("/", File.separator).replace("\\", File.separator)
        val filterRegex: MutableList<String> = mutableListOf()
        filterRegex.addAll(mutableListOf(
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}tmp${File.separator}.*",
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}upload${File.separator}.*",
                CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}download${File.separator}.*"))
        println(pathFilter(filterRegex, path))
    }

    /**
     * 文件路径过滤
     *
     * @param filterRegex 路径
     * @param path        待匹配路径
     * @return true-允许下载 false-不允许下载
     */
    private fun pathFilter(filterRegex: List<String>, path: String): Boolean {
        path.apply {
            for (regex in filterRegex) {
                if (CommonTools.regexPattern(regex.replace("\\", "/"), this.replace("\\", "/"))) {
                    return true
                }
            }
        }
        return false
    }

    @Test
    fun testRegex(){
        val regex = "^1.*0$"
        println(CommonTools.regexPattern(regex,"nifosnai"))
        println(CommonTools.regexPattern(regex,"1nfdosa"))
        println(CommonTools.regexPattern(regex,"1nfdosa0"))
    }

    @Test
    fun moneyToCn() {
        val money = BigDecimal.valueOf(32109)
        println(CommonTools.moneyToCn(money.setScale(2, RoundingMode.HALF_UP).toDouble()))
    }

    @Test
    fun numberToCn() {
        val number1 = BigDecimal.valueOf(32109.21)
        val number2 = BigDecimal.valueOf(50000.00)
        val number3 = BigDecimal.valueOf(748.59)
        val number4 = BigDecimal.valueOf(32109)
        val number5 = BigDecimal.valueOf(5109.1)
        val number6 = BigDecimal.valueOf(5000.01)
        println("$number1 ----> ${CommonTools.numberToCn(number1.toDouble(),2)}")
        println("$number2 ----> ${CommonTools.numberToCn(number2.toDouble(),2)}")
        println("$number3 ----> ${CommonTools.numberToCn(number3.toDouble(),2)}")
        println("$number4 ----> ${CommonTools.numberToCn(number4.toDouble(),2)}")
        println("$number5 ----> ${CommonTools.numberToCn(number5.toDouble(),5)}")
        println("$number6 ----> ${CommonTools.numberToCn(number6.toDouble(),2)}")
    }

    @Test
    fun testDelete(){
        CommonTools.doDeleteFile(File("D:\\test\\file\\新建文本文档.bat"),true,5000)
        runBlocking {
            delay(10000)
        }
    }

    @Test
    fun testByte(){
        println('s'.toByte())
        println('s'.code)
        println('s'.code.toByte())
    }
}
