package io.github.zhangbinhub.acp.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.PropertyNamingStrategies

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import net.lingala.zip4j.model.ZipParameters
import org.joda.time.DateTime
import io.github.zhangbinhub.acp.core.exceptions.OperateException
import io.github.zhangbinhub.acp.core.match.MoneyToCN
import io.github.zhangbinhub.acp.core.match.NumberToCN
import io.github.zhangbinhub.acp.core.match.Operate
import io.github.zhangbinhub.acp.core.security.key.KeyManagement
import io.github.zhangbinhub.acp.core.tools.CommonUtils

import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object CommonTools {

    @JvmStatic
    fun initTools() = CommonUtils.init()

    @JvmStatic
    fun initTools(deleteFileWaitTime: Long, absPathPrefix: String, userPathPrefix: String, fontPath: String) =
        CommonUtils.init(deleteFileWaitTime, absPathPrefix, userPathPrefix, fontPath)

    @JvmStatic
    fun getFileContentForByte(filePath: String): ByteArray? =
        CommonUtils.getFileContentForByte(filePath)

    @JvmStatic
    @JvmOverloads
    fun getFileContent(filePath: String, charset: String? = null): String? =
        CommonUtils.getFileContent(filePath, charset ?: getDefaultCharset())

    @JvmStatic
    @JvmOverloads
    fun getFileContentForText(filePath: String, charset: String? = null): String? =
        CommonUtils.getFileContentForText(filePath, charset ?: getDefaultCharset())

    @JvmStatic
    @JvmOverloads
    fun contentWriteToFile(filePath: String, content: String, charset: String? = null, append: Boolean = false): File? =
        CommonUtils.contentWriteToFile(
            filePath, content.toByteArray(
                Charset.forName(
                    charset
                        ?: getDefaultCharset()
                )
            ), append
        )

    @JvmStatic
    @JvmOverloads
    fun contentWriteToFile(filePath: String, content: ByteArray, append: Boolean = false): File? =
        CommonUtils.contentWriteToFile(filePath, content, append)

    @JvmStatic
    @JvmOverloads
    fun contentWriteToFile(file: File, content: String, charset: String? = null, append: Boolean = false) =
        CommonUtils.contentWriteToFile(
            file, content.toByteArray(
                Charset.forName(
                    charset
                        ?: getDefaultCharset()
                )
            ), append
        )

    @JvmStatic
    @JvmOverloads
    fun contentWriteToFile(file: File, content: ByteArray, append: Boolean = false) =
        CommonUtils.contentWriteToFile(file, content, append)

    @JvmStatic
    fun getFileExt(fileName: String): String {
        return CommonUtils.getFileExt(fileName)
    }

    @JvmStatic
    fun getFontFold(): String = CommonUtils.fontPath

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    @JvmStatic
    fun replaceVar(varFormula: String, data: Map<String, String>): String = CommonUtils.replaceVar(varFormula, data)

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    @JvmStatic
    fun getDefaultCharset(): String = CommonUtils.defaultCharset

    /**
     * 获取资源文件的输入流
     *
     * @param fileName 文件路径
     * @return 输入流
     */
    @JvmStatic
    fun getResourceInputStream(fileName: String): InputStream? = CommonUtils.getResourceInputStream(fileName)

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    @JvmStatic
    @JvmOverloads
    fun getProperties(key: String, defaultValue: String = ""): String = CommonUtils.getProperties(key, defaultValue)

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    @JvmStatic
    fun isAbsPath(path: String): Boolean = CommonUtils.isAbsPath(path)

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    @JvmStatic
    fun getAbsPath(srcPath: String): String = CommonUtils.getAbsPath(srcPath)

    /**
     * 将相对路径格式化为绝对路径，相对于 java path
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    @JvmStatic
    fun formatAbsPath(path: String): String = CommonUtils.formatAbsPath(path)

    /**
     * 获取WebRoot绝对路径
     *
     * @return WebRoot绝对路径
     */
    @JvmStatic
    fun getWebRootAbsPath(): String = CommonUtils.getWebRootAbsPath()

    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    @JvmStatic
    fun getUuid(): String = CommonUtils.getUuid()

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    @JvmStatic
    fun getUuid32(): String = CommonUtils.getUuid32()

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    @JvmStatic
    fun getUuid16(): String = CommonUtils.getUuid16()

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    @JvmStatic
    fun getUuid8(): String = CommonUtils.getUuid8()

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    @JvmStatic
    fun getRandomString(length: Int): String = KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length)

    /**
     * 正则表达式匹配字符串
     *
     * @param regex  正则表达式
     * @param srcStr 待匹配字符串
     * @return true|false
     */
    @JvmStatic
    fun regexPattern(regex: String, srcStr: String): Boolean = CommonUtils.regexPattern(regex, srcStr)

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    @JvmStatic
    fun isNullStr(src: String?): Boolean = CommonUtils.isNullStr(src)

    /**
     * 源字符串中每到指定长度时就插入子字符串
     *
     * @param src          源字符串
     * @param length       分隔长度
     * @param insertString 插入的子字符串
     * @return 目标字符串
     */
    @JvmStatic
    fun autoInsertString(src: String, length: Int, insertString: String): String =
        CommonUtils.autoInsertString(src, length, insertString)

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param string 填充字符串
     * @return 填充后的字符串
     */
    @JvmStatic
    fun strFillIn(str: String, number: Int, flag: Int, string: String): String =
        CommonUtils.strFillIn(str, number, flag, string)

    /**
     * 判断字符串是否在数组中
     *
     * @param str        源字符串
     * @param array      字符串数组
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    @JvmStatic
    @JvmOverloads
    fun strInArray(str: String, array: Array<String>, ignoreCase: Boolean = false): Boolean =
        CommonUtils.strInArray(str, array, ignoreCase)

    /**
     * 判断字符串是否在列表中
     *
     * @param str        源字符串
     * @param arrayList  字符串列表
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    @JvmStatic
    @JvmOverloads
    fun strInList(str: String, arrayList: List<String>, ignoreCase: Boolean = false): Boolean =
        CommonUtils.strInList(str, arrayList, ignoreCase)

    /**
     * 获取指定格式的时间字符串
     *
     * @param dateTime       DateTime 实例
     * @param dateTimeFormat 格式
     * @return 格式化的时间格式
     */
    @JvmStatic
    @JvmOverloads
    fun getDateTimeString(dateTime: DateTime? = null, dateTimeFormat: String = ""): String =
        CommonUtils.getDateTimeString(dateTime, dateTimeFormat)

    /**
     * 获取当前时刻的 DateTime 实例
     *
     * @return DateTime 实例
     */
    @JvmStatic
    fun getNowDateTime(): DateTime = CommonUtils.getNowDateTime()

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    @JvmStatic
    fun getNowString(): String = CommonUtils.getNowString()

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    @JvmStatic
    fun getNowTimeString(): String = CommonUtils.getNowTimeString()

    /**
     * 计算四则运算表达式结果
     *
     * @param calculateStr 表达式字符串
     * @return 结果
     */
    @JvmStatic
    @Throws(OperateException::class)
    fun doCalculate(calculateStr: String): Double {
        val operate = Operate()
        return operate.calculate(calculateStr)
    }

    /**
     * 金额转换为汉语中人民币的大写
     *
     * @param money 金额
     * @return 大写字符串
     */
    @JvmStatic
    fun moneyToCn(money: Double): String = MoneyToCN.moneyToCn(money)

    /**
     * 金额转换为汉语的大写
     *
     * @param number 数字
     * @param precision 保留小数位数，默认2
     * @return 大写字符串
     */
    @JvmStatic
    @JvmOverloads
    fun numberToCn(number: Double, precision: Int = 2): String = NumberToCN.numberToCn(number, precision)

    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    @JvmStatic
    fun getJsonFromStr(src: String): JsonNode = CommonUtils.getJsonFromStr(src)

    /**
     * json对象转为java对象
     *
     * @param jsonObj                json对象（JsonNode）
     * @param cls                    目标类
     * @param propertyNamingStrategy 名称处理规则
     * @return 目标对象
     */
    @JvmStatic
    @JvmOverloads
    fun <T> jsonToObject(
        jsonObj: JsonNode,
        cls: Class<T>,
        propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    ): T? =
        CommonUtils.jsonToObject(jsonObj, cls, propertyNamingStrategy)

    /**
     * 实体对象转换为json对象
     *
     * @param propertyNamingStrategy 名称处理规则
     * @param instance               实体对象（只持Map对象）
     * @return json对象
     */
    @JvmStatic
    @JvmOverloads
    fun objectToJson(
        instance: Any,
        propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    ): JsonNode =
        CommonUtils.objectToJson(instance, propertyNamingStrategy)

    /**
     * 下划线转驼峰
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    @JvmStatic
    fun toCamel(str: String): String = CommonUtils.toCamel(str).toString()


    /**
     * 驼峰转下划线
     *
     * @param str 待处理字符串
     * @return 转换后字符串
     */
    @JvmStatic
    fun toUnderline(str: String): String = CommonUtils.toUnderline(str).toString()

    /**
     * 压缩文件
     *
     * @param fileNames      需要压缩的文件路径数组，可以是全路径也可以是相对于WebRoot的路径
     * @param resultFileName 生成的目标文件全路径
     * @param deleteFile     压缩完后是否删除原文件
     * @param password       压缩密码
     * @param zipParameters  压缩参数
     * @return 目标文件绝对路径
     */
    @JvmStatic
    @JvmOverloads
    fun filesToZip(
        fileNames: List<String>,
        resultFileName: String,
        deleteFile: Boolean = false,
        password: String? = null,
        zipParameters: ZipParameters? = null
    ): String =
        CommonUtils.filesToZip(fileNames, resultFileName, deleteFile, password, zipParameters)

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param deleteFile   解压完成是否删除压缩文件
     * @param password     压缩密码
     */
    @JvmStatic
    @JvmOverloads
    fun zipToFiles(zipFileName: String, parentFold: String, deleteFile: Boolean = false, password: String? = null) =
        CommonUtils.zipToFiles(zipFileName, parentFold, deleteFile, password)

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isAsync   是否异步删除
     * @param waitTime 异步删除等待时间
     */
    @JvmStatic
    @JvmOverloads
    fun doDeleteFile(file: File, isAsync: Boolean = false, waitTime: Long? = null) =
        CommonUtils.doDeleteFile(file, isAsync, waitTime)
}