package com.github.zhangbinhub.acp.core.tools

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import kotlinx.coroutines.*
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import org.joda.time.DateTime
import com.github.zhangbinhub.acp.core.conf.AcpProperties
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.task.timer.Calculation
import java.io.*
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern


/**
 * @author zhang by 08/07/2019
 * @since JDK 11
 */
object CommonUtils {

    private var pps: Properties? = null

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    const val defaultCharset = "utf-8"

    var fontPath: String = "files/resource/font"

    private var deleteFileWaitTime: Long = 1200000L

    private var absPathPrefix: String = "abs:"

    private var userPathPrefix: String = "user:"

    private val log = LogFactory.getInstance(CommonUtils::class.java)

    fun init() {
        log.info("default charset is : $defaultCharset")
        absPathPrefix = getProperties("absPath.prefix", "abs:")
        userPathPrefix = getProperties("userPath.prefix", "user:")
        fontPath = getProperties("fonts.fold", "files/resource/font")
        deleteFileWaitTime = getProperties("deleteFile.waitTime", "1200000").toLong()
    }

    fun init(deleteFileWaitTime: Long, absPathPrefix: String, userPathPrefix: String, fontPath: String) {
        CommonUtils.deleteFileWaitTime = deleteFileWaitTime
        CommonUtils.absPathPrefix = absPathPrefix
        if (isNullStr(CommonUtils.absPathPrefix)) {
            CommonUtils.absPathPrefix = getProperties("absPath.prefix", "abs:")
        }
        CommonUtils.userPathPrefix = userPathPrefix
        if (isNullStr(CommonUtils.userPathPrefix)) {
            CommonUtils.userPathPrefix = getProperties("userPath.prefix", "user:")
        }
        CommonUtils.fontPath = fontPath
        if (isNullStr(CommonUtils.fontPath)) {
            CommonUtils.fontPath = getProperties("fonts.fold", "files/resource/font")
        }
    }

    /**
     * 初始化系统配置文件
     */
    private fun initSystemProperties() {
        pps = try {
            pps ?: AcpProperties.getInstance()
        } catch (e: Exception) {
            log.error("load acp.properties failed!")
            null
        }
    }

    /**
     * 获取文件中的内容（NIO方式按字节获取）
     *
     * @param filePath 文件绝对路径
     * @return 内容
     */
    fun getFileContentForByte(filePath: String): ByteArray? {
        var fis: FileInputStream? = null
        var out: ByteArrayOutputStream? = null
        var channel: FileChannel? = null
        return try {
            fis = FileInputStream(filePath)
            out = ByteArrayOutputStream()
            channel = fis.channel
            val buffer = ByteBuffer.allocate(1024 * 1024)
            while (true) {
                val size = channel.read(buffer)
                if (size == -1) {
                    break
                }
                out.write(buffer.array(), 0, size)
                buffer.clear()
            }
            out.toByteArray()
        } catch (e: java.lang.Exception) {
            log.error(e.message, e)
            null
        } finally {
            try {
                out?.close()
                channel?.close()
                fis?.close()
            } catch (e: IOException) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * 获取文件中的内容
     *
     * @param filePath 文件绝对路径
     * @param charset 字符编码
     * @return 内容
     */
    fun getFileContent(filePath: String, charset: String): String? =
        getFileContentForByte(filePath)?.let {
            String(it, Charset.forName(charset))
        }

    /**
     * 获取文件中的内容
     *
     * @param filePath 文件绝对路径
     * @param charset 字符编码
     * @return 内容
     */
    fun getFileContentForText(filePath: String, charset: String): String? {
        var reader: BufferedReader? = null
        return try {
            reader = BufferedReader(InputStreamReader(FileInputStream(filePath), Charset.forName(charset)))
            reader.readText()
        } catch (e: java.lang.Exception) {
            log.error(e.message, e)
            null
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * 内容写入文件
     * @param filePath 文件路径
     * @param content 内容
     * @param append 是否追加写入
     */
    fun contentWriteToFile(filePath: String, content: ByteArray, append: Boolean): File? {
        val tmpFile = File(filePath)
        val fold = tmpFile.parentFile
        if (!fold.exists() && !fold.mkdirs()) {
            log.error("mkdir field：" + fold.canonicalFile)
            return null
        }
        contentWriteToFile(tmpFile, content, append)
        return tmpFile
    }

    /**
     * 内容写入文件
     * @param file 文件
     * @param content 内容
     * @param append 是否追加写入
     */
    fun contentWriteToFile(file: File, content: ByteArray, append: Boolean) {
        var out: FileOutputStream? = null
        var channel: FileChannel? = null
        try {
            out = FileOutputStream(file, append)
            channel = out.channel
            channel.write(ByteBuffer.wrap(content))
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            try {
                channel?.close()
                out?.close()
            } catch (e: IOException) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名称
     * @return 扩展名（小写）
     */
    fun getFileExt(fileName: String): String =
        if (fileName.lastIndexOf(".") > -1) {
            fileName.substring(fileName.lastIndexOf(".") + 1).lowercase()
        } else {
            ""
        }

    /**
     * 获取 WebRoot 绝对路径
     *
     * @return WebRoot 绝对路径
     */
    fun getWebRootAbsPath(): String = try {
        var classPath = URLDecoder.decode(CommonUtils::class.java.getResource("/")?.path ?: "", defaultCharset)
        var indexWebInf = classPath.indexOf("WEB-INF")
        if (indexWebInf == -1) {
            indexWebInf = classPath.indexOf("bin")
        }
        var webRootPath = classPath
        if (indexWebInf != -1) {
            webRootPath = webRootPath.substring(0, indexWebInf)
        }
        when {
            webRootPath.startsWith("jar") -> webRootPath = webRootPath.substring(10)
            webRootPath.startsWith("file") -> webRootPath = webRootPath.substring(6)
            else -> {
                CommonUtils::class.java.classLoader.getResource("/")?.let {
                    classPath = it.path
                    indexWebInf = classPath.indexOf("WEB-INF")
                    if (indexWebInf == -1) {
                        indexWebInf = classPath.indexOf("bin")
                    }
                    webRootPath = if (indexWebInf != -1) {
                        classPath.substring(0, indexWebInf)
                    } else {
                        classPath
                    }
                }
            }
        }
        if (webRootPath.endsWith("/")) {
            webRootPath = webRootPath.substring(0, webRootPath.length - 1)
        }
        webRootPath = webRootPath.replace("/", File.separator).replace("\\", File.separator)
        if (webRootPath.startsWith(File.separator)) {
            webRootPath = webRootPath.substring(1)
        }
        if (webRootPath.contains("!")) {
            webRootPath = webRootPath.substring(0, webRootPath.indexOf("!"))
            webRootPath = webRootPath.substring(0, webRootPath.lastIndexOf(File.separator))
        }
        if (isNullStr(webRootPath)) {
            webRootPath = File.separator
        } else {
            if (File.separator.equals("/", ignoreCase = true)) {
                if (!webRootPath.startsWith(File.separator)) {
                    webRootPath = File.separator + webRootPath
                }
            }
        }
        webRootPath
    } catch (e: Exception) {
        log.error(e.message, e)
        log.error("webRootAbsPath=\"\"")
        ""
    }


    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    fun getUuid(): String = UUID.randomUUID().toString().uppercase()

    /**
     * 获取uuid
     *
     * @param length 字符串长度
     * @return 结果
     */
    private fun getUuid(length: Int): String = RandomStringGenerator.Builder()
        .withinRange(33, 126)
        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
        .build().generate(length)

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    fun getUuid32(): String = getUuid().replace("-", "")

    /**
     * 获取24位短uuid
     *
     * @return 结果
     */
    fun getUuid24(): String = getUuid(24)

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    fun getUuid16(): String = getUuid(16)

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    fun getUuid8(): String = getUuid(8)

    /**
     * 获取当前时刻的 DateTime 实例
     *
     * @return DateTime 实例
     */
    fun getNowDateTime(): DateTime = DateTime()

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    fun getNowString(): String = getDateTimeString(dateTimeFormat = Calculation.DATE_FORMAT)

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    fun getNowTimeString(): String = getDateTimeString(dateTimeFormat = Calculation.DATETIME_FORMAT)

    /**
     * 获取指定格式的时间字符串
     *
     * @param dateTime       DateTime 实例
     * @param dateTimeFormat 格式
     * @return 格式化的时间格式
     */
    fun getDateTimeString(dateTime: DateTime? = null, dateTimeFormat: String = ""): String {
        val time = dateTime ?: getNowDateTime()
        var format = dateTimeFormat
        if (isNullStr(dateTimeFormat)) {
            format = Calculation.DATETIME_FORMAT
        }
        return time.toString(format)
    }

    /**
     * 获取资源文件的输入流
     *
     * @param fileName 文件路径
     * @return 输入流
     */
    fun getResourceInputStream(fileName: String): InputStream? {
        var name = fileName.replace("\\", "/")
        if (name.startsWith("/")) {
            name = name.substring(1)
        }
        return CommonUtils::class.java.classLoader.getResourceAsStream(name)
    }

    /**
     * 获取配置信息
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    fun getProperties(key: String, defaultValue: String = ""): String {
        initSystemProperties()
        return if (pps != null) {
            pps!!.getProperty(key, defaultValue)
        } else {
            defaultValue
        }
    }

    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    fun isAbsPath(path: String): Boolean {
        return path.startsWith(absPathPrefix)
    }

    /**
     * 获取绝对路径
     *
     * @param srcPath 路径
     * @return 绝对路径
     */
    fun getAbsPath(srcPath: String): String {
        val path = srcPath.replace("\\", File.separator).replace("/", File.separator)
        return when {
            isAbsPath(path) && path.startsWith(absPathPrefix) -> path.substring(absPathPrefix.length)
            !isAbsPath(path) && path.startsWith(userPathPrefix) -> System.getProperty("user.home") + path.substring(
                userPathPrefix.length
            )
            !isAbsPath(path) -> formatAbsPath(path)
            else -> path
        }
    }

    /**
     * 将相对路径格式化为绝对路径，相对于 java path
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    fun formatAbsPath(path: String): String {
        var srcPath = path.replace("\\", File.separator).replace("/", File.separator)
        if (srcPath.startsWith(File.separator)) {
            srcPath = srcPath.substring(File.separator.length)
        }
        return File(srcPath).canonicalPath
    }

    /**
     * 正则表达式匹配字符串
     *
     * @param regex  正则表达式
     * @param srcStr 待匹配字符串
     * @return true|false
     */
    fun regexPattern(regex: String, srcStr: String): Boolean {
        return !isNullStr(regex) && Regex(regex).matches(srcStr)
    }

    /**
     * 表达式变量替换
     *
     * @param varFormula 变量表达式:格式“${变量名}” 或带有变量格式的字符串
     * @param data       数据集
     * @return 目标字符串
     */
    fun replaceVar(varFormula: String, data: Map<String, String>): String {
        var formula = varFormula
        var tmpVar = varFormula
        if (tmpVar.contains("\${")) {
            if (tmpVar.contains("}")) {
                var begin = tmpVar.indexOf("\${")
                while (begin != -1) {
                    tmpVar = tmpVar.substring(begin + 2)
                    if (tmpVar.contains("}")) {
                        val varKey = tmpVar.substring(0, tmpVar.indexOf("}"))
                        if (data.containsKey(varKey)) {
                            formula = formula.replace("\${$varKey}", data.getOrElse(varKey) { "" })
                        }
                        tmpVar = tmpVar.substring(tmpVar.indexOf("}") + 1)
                        begin = tmpVar.indexOf("\${")
                    } else {
                        begin = -1
                    }
                }
            }
            return formula
        } else {
            return formula
        }
    }

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    fun isNullStr(src: String?): Boolean {
        return src == null || src.isNullOrBlank()
    }

    /**
     * 源字符串中每到指定长度时就插入子字符串
     *
     * @param src          源字符串
     * @param length       分隔长度
     * @param insertString 插入的子字符串
     * @return 目标字符串
     */
    fun autoInsertString(src: String, length: Int, insertString: String): String {
        var result = src
        val maxLength = src.length
        for (i in 0 until maxLength / length) {
            val endIndex = (i + 1) * length + i * insertString.length
            result = result.substring(0, endIndex) + insertString + result.substring(endIndex)
        }
        if (result.lastIndexOf(insertString) == result.length - insertString.length) {
            result = result.substring(0, result.length - insertString.length)
        }
        return result
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param string 填充字符串
     * @return 填充后的字符串
     */
    fun strFillIn(str: String, number: Int, flag: Int, string: String): String {
        val byteLen = str.toByteArray().size
        val strLen = str.length
        return when (flag) {
            0 -> StringUtils.leftPad(str, number - (byteLen - strLen), string)
            1 -> StringUtils.rightPad(str, number - (byteLen - strLen), string)
            else -> ""
        }
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str        源字符串
     * @param array      字符串数组
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    fun strInArray(str: String, array: Array<String>, ignoreCase: Boolean = false): Boolean {
        for (anArray in array) {
            if (when {
                    ignoreCase && str.equals(anArray, ignoreCase = true) -> true
                    !ignoreCase && str == anArray -> true
                    else -> false
                }
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str        源字符串
     * @param arrayList  字符串列表
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    fun strInList(str: String, arrayList: List<String>, ignoreCase: Boolean = false): Boolean =
        if (ignoreCase) {
            arrayList.stream().filter { anArrayList -> anArrayList.equals(str, ignoreCase = true) }.count() > 0
        } else {
            arrayList.stream().filter { anArrayList -> anArrayList == str }.count() > 0
        }


    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    fun getJsonFromStr(src: String): JsonNode {
        val mapper = ObjectMapper()
        var jsonNode: JsonNode = mapper.createObjectNode()
        try {
            jsonNode = mapper.readTree(src)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        return jsonNode
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj                json对象（JsonNode）
     * @param cls                    目标类
     * @param propertyNamingStrategy 名称处理规则
     * @return 目标对象
     */
    fun <T> jsonToObject(
        jsonObj: JsonNode,
        cls: Class<T>,
        propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    ): T? {
        val mapper = ObjectMapper()
        var instance: T? = null
        try {
            mapper.propertyNamingStrategy = propertyNamingStrategy
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            instance = mapper.readValue(jsonObj.toString(), cls)
        } catch (e: IOException) {
            log.error(e.message, e)
        }
        return instance
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance               实体对象（只持Map对象）
     * @param propertyNamingStrategy 名称处理规则
     * @return json对象
     */
    fun objectToJson(
        instance: Any,
        propertyNamingStrategy: PropertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    ): JsonNode {
        val jsonNodeFactory = JsonNodeFactory(true)
        val mapper = ObjectMapper()
        mapper.nodeFactory = jsonNodeFactory
        var jsonNode: JsonNode
        try {
            mapper.propertyNamingStrategy = propertyNamingStrategy
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            jsonNode = getJsonFromStr(mapper.writeValueAsString(instance))
        } catch (e: JsonProcessingException) {
            log.error(e.message, e)
            jsonNode = mapper.createObjectNode()
        }

        return jsonNode
    }

    /**
     * 下划线转驼峰
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    fun toCamel(str: String): StringBuffer {
        //利用正则删除下划线，把下划线后一位改成大写
        val pattern = Pattern.compile("_(\\w)")
        val matcher = pattern.matcher(str)
        var sb = StringBuffer(str)
        if (matcher.find()) {
            sb = StringBuffer()
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).uppercase())
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb)
        } else {
            return sb
        }
        return toCamel(sb.toString())
    }


    /**
     * 驼峰转下划线
     *
     * @param str 待处理字符串
     * @return 转换后字符串
     */
    fun toUnderline(str: String): StringBuffer {
        val pattern = Pattern.compile("[A-Z]")
        val matcher = pattern.matcher(str)
        var sb = StringBuffer(str)
        if (matcher.find()) {
            sb = StringBuffer()
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).lowercase())
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb)
        } else {
            return sb
        }
        return toUnderline(sb.toString())
    }

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
    fun filesToZip(
        fileNames: List<String>,
        resultFileName: String,
        deleteFile: Boolean = false,
        password: String? = null,
        zipParameters: ZipParameters? = null
    ): String {
        val startTime = System.currentTimeMillis()
        var endTime: Long = 0
        try {
            val resultFileParent = File(resultFileName).parentFile
            if (!resultFileParent.exists()) {
                if (!resultFileParent.mkdirs()) {
                    log.error("mkdir failed : " + resultFileParent.canonicalPath)
                }
            }
            val files: MutableList<File> = mutableListOf()
            val folds: MutableList<File> = mutableListOf()
            fileNames.forEach { name ->
                val file = File(name)
                if (file.isDirectory) {
                    folds.add(file)
                } else {
                    files.add(file)
                }
            }
            val zipFile = if (password == null) {
                ZipFile(resultFileName)
            } else {
                ZipFile(resultFileName, password.toCharArray())
            }
            //设置压缩文件参数
            val parameters = zipParameters ?: ZipParameters().apply {
                this.compressionMethod = CompressionMethod.DEFLATE
                this.compressionLevel = CompressionLevel.NORMAL
                password?.let {
                    this.isEncryptFiles = true
                    this.encryptionMethod = EncryptionMethod.ZIP_STANDARD
                    this.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
                }
            }
            if (files.isNotEmpty()) {
                zipFile.addFiles(files, parameters)
            }
            folds.forEach { fold -> zipFile.addFolder(fold, parameters) }
            if (deleteFile) {
                files.forEach { file -> doDeleteFile(file) }
                folds.forEach { file -> doDeleteFile(file) }
            }
            log.info("compress success")
            endTime = System.currentTimeMillis()
            return resultFileName
        } catch (e: Exception) {
            log.error("file compress Exception:" + e.message, e)
            endTime = System.currentTimeMillis()
            return ""
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms")
        }
    }

    /**
     * 解压缩文件
     *
     * @param zipFileName  zip压缩文件名
     * @param parentFold   解压目标文件夹
     * @param deleteFile   解压完成是否删除压缩文件
     * @param password     压缩密码
     */
    fun zipToFiles(zipFileName: String, parentFold: String, deleteFile: Boolean = false, password: String? = null) {
        val startTime = System.currentTimeMillis()
        var endTime: Long = 0
        try {
            val resultFileParent = File(parentFold)
            if (!resultFileParent.exists()) {
                if (!resultFileParent.mkdirs()) {
                    log.error("mkdir failed : " + resultFileParent.canonicalPath)
                }
            }
            val zipFile = if (password == null) {
                ZipFile(zipFileName)
            } else {
                ZipFile(zipFileName, password.toCharArray())
            }
            zipFile.extractAll(parentFold)
            if (deleteFile) {
                doDeleteFile(File(zipFileName))
            }
            log.info("decompress success")
            endTime = System.currentTimeMillis()
        } catch (e: Exception) {
            log.error("file decompress Exception:" + e.message, e)
            endTime = System.currentTimeMillis()
        } finally {
            log.info("time consuming : " + (endTime - startTime) + " ms")
        }
    }

    /**
     * 删除文件
     *
     * @param file     待删除文件
     * @param isAsync   是否异步删除
     * @param waitTime 异步删除等待时间（单位毫秒）
     */
    fun doDeleteFile(file: File, isAsync: Boolean = false, waitTime: Long? = null) {
        if (isAsync) {
            var time = deleteFileWaitTime
            if (waitTime != null) {
                time = waitTime
            }
            Thread {
                runBlocking(Dispatchers.Unconfined) {
                    log.info("ready delete file [" + file.canonicalPath + "],waiting " + time / 1000 + " seconds")
                    delay(time)
                    doDeleteFileOrDir(file)
                }
            }.start()
        } else {
            doDeleteFileOrDir(file)
        }
    }

    private fun doDeleteFileOrDir(file: File): Boolean {
        try {
            if (file.exists()) {
                if (file.isDirectory) {
                    val children = file.list()
                    if (children != null) {
                        for (aChildren in children) {
                            val success = doDeleteFileOrDir(File(file, aChildren))
                            if (!success) {
                                return false
                            }
                        }
                    }
                }
                return file.delete()
            } else {
                return true
            }
        } catch (e: Exception) {
            log.error("delete file Exception:" + e.message, e)
            return false
        }
    }

}