package pers.acp.packet.iso8583

import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory

import java.io.UnsupportedEncodingException
import java.util.TreeMap
import kotlin.math.abs

/**
 * 简单介绍下ISO8583。
 * 这个东西说白了就是一种数据结构。我们定义一种规则把一堆东西放进去，再按照规则
 * 把数据正确拿出来。这就是报文的实质。
 *
 *
 * ISO8583报文的结构是：前面有16字节（128位）位图数据，后面就是数据。
 * 报文最多有128个域（字段）。具体的一个报文不会有这么多，一般是几个域。
 * 有哪几个就记录在位图中。而且域有定长和变长之分。
 * 这些都是事先定义好的，具体可以看我写的properties定义文件.
 *
 *
 * 位图转化成01字符串就是128个，如果某一位是1，代表这个域有值，然后按照properties定义的规则取值。
 * 如果是0，则这个域没有值。
 *
 *
 * 再说定长和变长。
 * 定长域(定长比较好理解，一个字段规定是N位，那么字段值绝对不能超过N位，不足N位就在后面补空格)
 * 变长域(变长域最后组装成的效果：例如变长3位，定义var3，这里的3是指长度值占3位，字段值是123456，最后结果就是006123456)
 * 注意（变长的长度按照域值得字节长度计算，而不是按照域值字符串长度算！）
 *
 *
 * 从网上不难找到ISO8583报文的介绍，这里就不多说了。
 * 但是具体解析和组装的代码还真不好找，所以本人就写了一个让刚接触ISO8583报文的人更好入门。
 *
 *
 *
 *
 *
 *
 * 解析的容器，我使用了Map，具体到工作中，还是要换成其他的容器的。
 * 报文定义说明：iso8583.properties
 * 例如
 * FIELD031 = string,10
 * FIELD032 = string,VAR2
 *
 *
 * FIELD031是定长，长度是10
 * FIELD032是变长，长度值占2位，也就是说长度值最大99，也就是域值最大长度99.
 *
 * @author zhangbin
 */
object Iso8583Packet {

    private val log = LogFactory.getInstance(Iso8583Packet::class.java)

    private var packetEncoding = CommonTools.getDefaultCharset()//报文编码 UTF-8 GBK

    private var map8583Definition: MutableMap<String, String> = mutableMapOf()// 8583报文128域定义器

    /**
     * 8583报文初始位图:128位01字符串
     *
     * @return 位图
     */
    private fun initBitMap(): String = ("10000000" + "00000000" + "00000000" + "00000000" +
            "00000000" + "00000000" + "00000000" + "00000000" +
            "00000000" + "00000000" + "00000000" + "00000000" +
            "00000000" + "00000000" + "00000000" + "00000000")

    init {
        try {
            val iso8583FieldProperties = Iso8583FieldProperties.getInstance()
            iso8583FieldProperties?.let {
                iso8583FieldProperties.forEach { key, value -> map8583Definition[key.toString()] = value.toString() }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    /**
     * 组装8583报文
     *
     * @param filedMap 域值
     */
    @JvmStatic
    fun make8583(filedMap: TreeMap<String, String>): ByteArray? =
            try {
                val bitMap128 = initBitMap()//获取初始化的128位图
                //按照8583定义器格式化各个域的内容
                val all = formatValueTo8583(filedMap, bitMap128)
                // 获取上送报文内容
                getWhole8583Packet(all)
            } catch (e: Exception) {
                log.error(e.message, e)
                null
            }

    /**
     * 获取完整的8583报文体（128域）
     *
     * @param all 内容
     * @return 字节数组
     */
    private fun getWhole8583Packet(all: Map<String, Any>?): ByteArray? {
        if (all == null || all["formattedFiledMap"] == null || all["bitMap128"] == null) {
            return null
        }
        try {
            val bitMap128 = all["bitMap128"] as String
            // 128域位图二进制字符串转16位16进制
            val bitmaps = get16BitByteFromStr(bitMap128)
            val pacBody = all["formattedFiledMap"] as TreeMap<*, *>
            val last128 = StringBuilder()
            val it = pacBody.keys.iterator()
            while (it.hasNext()) {
                val key = it.next() as String
                val value = pacBody[key] as String
                last128.append(value)
            }
            val bitContent = last128.toString().toByteArray(charset(packetEncoding))//域值
            //组装
            var package8583: ByteArray?
            package8583 = arrayAppend(null, bitmaps)
            package8583 = arrayAppend(package8583, bitContent)
            return package8583
        } catch (e: Exception) {
            log.error(e.message, e)
            return null
        }
    }

    private fun formatValueTo8583(filedMap: TreeMap<String, String>, bitMap128: String): Map<String, Any>? {
        var bitMap128Local = bitMap128
        val all = HashMap<String, Any>()
        val formattedFiledMap = TreeMap<String, String>()//格式化结果
        val it = filedMap.keys.iterator()
        while (it.hasNext()) {
            val fieldName = it.next()//例如FIELD005
            var fieldValue: String? = filedMap[fieldName]//字段值
            try {
                if (fieldValue == null) {
                    log.error("报文域 {$fieldName}为空值")
                    return null
                }
                //将域值编码转换，保证报文编码统一
                fieldValue = String(fieldValue.toByteArray(charset(packetEncoding)), charset(packetEncoding))
                // 数据域名称FIELD开头的为128域
                if (fieldName.startsWith("FIELD")) {
                    val fieldNo = fieldName.substring(5, 8)//例如005
                    // 组二进制位图串
                    bitMap128Local = change16bitMapFlag(fieldNo, bitMap128Local)
                    // 获取域定义信息
                    val fieldDef = map8583Definition["FIELD$fieldNo"]!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    var defLen = fieldDef[1]//长度定义,例20
                    var isFixLen = true//是否定长判断
                    if (defLen.startsWith("VAR")) {//变长域
                        isFixLen = false
                        defLen = defLen.substring(3)//获取VAR2后面的2
                    }
                    val fieldLen = fieldValue.toByteArray(charset(packetEncoding)).size//字段值得实际长度
                    // 判断是否为变长域
                    if (!isFixLen) {// 变长域(变长域最后组装成的效果：例如变长3位，定义var3，这里的3是指长度值占3位，字段值是123456，最后结果就是006123456)
                        val defLen1 = defLen.toInt()
                        if (fieldLen.toString().length > 10 * defLen1) {
                            log.error("字段" + fieldName + "的数据定义长度的长度为" + defLen + "位,长度不能超过" + 10 * defLen1)
                            return null
                        } else {
                            //将长度值组装入字段
                            fieldValue = getVaryLengthValue(fieldValue, defLen1)!! + fieldValue
                        }
                    } else {//定长域(定长比较好理解，一个字段规定是N位，那么字段值绝对不能超过N位，不足N位就在后面补空格)
                        val defLen2 = defLen.toInt()
                        if (fieldLen > defLen2) {
                            log.error("字段" + fieldName + "的数据定义长度为" + defLen + "位,长度不能超过" + defLen)
                            return null
                        } else {
                            fieldValue = getFixFieldValue(fieldValue, defLen2)//定长处理
                        }
                    }
                    log.info("组装后报文域 {" + fieldName + "}==" + fieldValue + "==,域长度:" + fieldValue!!.toByteArray(charset(packetEncoding)).size)
                }
                // 返回结果赋值
                if (filedMap.containsKey(fieldName)) {
                    if (formattedFiledMap.containsKey(fieldName)) {
                        formattedFiledMap.remove(fieldName)
                    }
                    formattedFiledMap[fieldName] = fieldValue
                } else {
                    log.error(fieldName + "配置文件中不存在!")
                }
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }//end for
        all["formattedFiledMap"] = formattedFiledMap
        all["bitMap128"] = bitMap128Local
        return all
    }


    /**
     * 解析8583报文
     *
     * @param content8583 报文内容
     */
    @JvmStatic
    fun analyze8583(content8583: ByteArray): Map<String, String> {
        val filedMap = TreeMap<String, String>()
        try {
            // 取位图
            var bitMap16byte = ByteArray(16)
            //记录当前位置,从位图后开始遍历取值
            var pos = 16
            val bitMap128Str: String
            if (Integer.toBinaryString(content8583[0].toInt() ushr 7).substring(24).equals("1", ignoreCase = true)) {
                System.arraycopy(content8583, 0, bitMap16byte, 0, 16)
            } else {
                bitMap16byte = ByteArray(8)
                pos = 8
                System.arraycopy(content8583, 0, bitMap16byte, 0, 8)
            }
            // 16位图转2进制位图128位字符串
            bitMap128Str = get16BitMapStr(bitMap16byte)
            // 遍历128位图，取值。注意从FIELD002开始
            for (i in 1 until bitMap128Str.length) {
                val filedValue: String//字段值
                val filedName = "FIELD" + getNumThree(i + 1)//FIELD005
                if (bitMap128Str[i] == '1') {
                    // 获取域定义信息
                    val fieldDef = map8583Definition[filedName]!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    var defLen = fieldDef[1]//长度定义,例20
                    var isFixLen = true//是否定长判断

                    if (defLen.startsWith("VAR")) {//变长域
                        isFixLen = false
                        defLen = defLen.substring(3)//获取VAR2后面的2
                    }
                    // 截取该域信息
                    if (!isFixLen) {//变长域
                        val defLen1 = defLen.toInt()//VAR2后面的2
                        val realLen1 = String(content8583, pos, defLen1, charset(packetEncoding))//报文中实际记录域长,例如16,023
                        val realAllLen = defLen1 + realLen1.toInt()//该字段总长度（包括长度值占的长度）
                        val filedValueByte = ByteArray(realLen1.toInt())
                        System.arraycopy(content8583, pos + defLen1, filedValueByte, 0, filedValueByte.size)
                        filedValue = String(filedValueByte, charset(packetEncoding))
                        pos += realAllLen//记录当前位置
                    } else {//定长域
                        val defLen2 = defLen.toInt()//长度值占的位数
                        filedValue = String(content8583, pos, defLen2, charset(packetEncoding))
                        pos += defLen2//记录当前位置
                    }
                    filedMap[filedName] = filedValue
                }
            }//end for
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        return filedMap
    }

    /**
     * 复制字符
     *
     * @param str   源字符串
     * @param count 数量
     * @return 目标字符串
     */
    private fun strCopy(str: String, count: Int): String {
        val sb = StringBuilder()
        for (i in 0 until count) {
            sb.append(str)
        }
        return sb.toString()
    }

    /**
     * 将setContent放入set（考虑到数组越界）
     *
     * @param set        字节数组
     * @param setContent 字节数组
     * @return 字节数组
     */
    private fun setToByte(set: ByteArray, setContent: ByteArray?): ByteArray {
        val res = ByteArray(set.size)
        if (setContent != null) {
            if (set.size >= setContent.size) {
                System.arraycopy(setContent, 0, res, 0, setContent.size)
            }
        }
        return res
    }

    @JvmStatic
    fun setToByte(set: ByteArray, setContentStr: String): ByteArray {
        var res = ByteArray(set.size)
        val setContent: ByteArray
        try {
            setContent = setContentStr.toByteArray(charset(packetEncoding))
            res = setToByte(res, setContent)
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message, e)
        }
        return res
    }

    private fun getPacketLen(len: Int): String {
        val res: String
        val lenStr = len.toString()
        val lenC = 4 - lenStr.length
        res = strCopy("0", lenC) + lenStr
        return res
    }

    @JvmStatic
    fun getPacketLen(lenStr: String?): String {
        var res = ""
        if (lenStr != null) {
            res = getPacketLen(lenStr.toInt())
        }
        return res
    }


    /**
     * 返回a和b的组合,实现累加功能
     *
     * @param a 字节数组
     * @param b 字节数组
     * @return 字节数组
     */
    private fun arrayAppend(a: ByteArray?, b: ByteArray?): ByteArray? {
        val aLen = a?.size ?: 0
        val bLen = b?.size ?: 0
        val c = ByteArray(aLen + bLen)
        if (aLen == 0 && bLen == 0) {
            return null
        } else if (aLen == 0) {
            System.arraycopy(b!!, 0, c, 0, b.size)
        } else if (bLen == 0) {
            System.arraycopy(a!!, 0, c, 0, a.size)
        } else {
            System.arraycopy(a!!, 0, c, 0, a.size)
            System.arraycopy(b!!, 0, c, a.size, b.size)
        }
        return c
    }


    /**
     * 改变128位图中的标志为1
     *
     * @param fieldNo 域编号
     * @param res     res
     * @return result
     */
    private fun change16bitMapFlag(fieldNo: String, res: String): String {
        var result = res
        val indexNo = fieldNo.toInt()
        result = result.substring(0, indexNo - 1) + "1" + result.substring(indexNo)
        return result
    }


    /**
     * 位图操作
     *
     *
     * 把16位图的字节数组转化成128位01字符串
     *
     * @param bitMap16 位图
     * @return 字符串
     */
    private fun get16BitMapStr(bitMap16: ByteArray): String {
        val bitMap128 = StringBuilder()
        // 16位图转2进制位图128位字符串
        for (aBitMap16 in bitMap16) {
            var bc = aBitMap16.toInt()
            bc = if (bc < 0) bc + 256 else bc
            val bitNaryStr = Integer.toBinaryString(bc)//二进制字符串
            // 左补零，保证是8位
            val rightBitNaryStr = strCopy("0", abs(8 - bitNaryStr.length)) + bitNaryStr//位图二进制字符串
            // 先去除多余的零，然后组装128域二进制字符串
            bitMap128.append(rightBitNaryStr)
        }
        return bitMap128.toString()
    }

    /**
     * 位图操作
     *
     *
     * 把128位01字符串转化成16位图的字节数组
     *
     * @param str128 字符串
     * @return 字节数组
     */
    private fun get16BitByteFromStr(str128: String?): ByteArray? {
        val bit16 = ByteArray(16)
        try {
            if (str128 == null || str128.length != 128) {
                return null
            }
            // 128域位图二进制字符串转16位16进制
            val tmp = str128.toByteArray(charset(packetEncoding))
            var weight: Int//权重
            val bytes = ByteArray(128)
            var i = 0
            var w = 0
            var j: Int
            while (i < 16) {
                weight = 0x0080
                j = 0
                while (j < 8) {
                    bytes[i] = (bytes[i].toInt() + ((tmp[w].toInt() - '0'.toInt()) * weight)).toByte()
                    weight /= 2
                    w++
                    j++
                }
                bit16[i] = bytes[i]
                i++
            }
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message, e)
        }

        return bit16
    }


    /**
     * 从完整的8583报文中获取位图（16字节数组）
     *
     * @param packet 字节数组
     * @return 字节数组
     */
    private fun getPacketHeaderMap(packet: ByteArray?): ByteArray? {
        val packetHeaderMap = ByteArray(16)
        if (packet == null || packet.size < 16) {
            return null
        }
        System.arraycopy(packet, 0, packetHeaderMap, 0, 16)
        return packetHeaderMap
    }

    /**
     * 从完整的8583报文中获取16位图，转化成128位的01字符串
     *
     * @param content8583 字节数组
     * @return 字符串
     */
    @JvmStatic
    fun get16BitMapFrom8583Byte(content8583: ByteArray): String? {
        // 取位图
        val bitMap16 = getPacketHeaderMap(content8583)
        return if (bitMap16 != null) {
            // 16位图转2进制位图128位字符串
            get16BitMapStr(bitMap16)
        } else {
            null
        }
    }

    //返回字段号码，例如005
    private fun getNumThree(i: Int): String {
        val len: String
        val iStr = i.toString()
        len = strCopy("0", 3 - iStr.length) + iStr
        return len
    }

    private fun getVaryLengthValue(valueStr: String?, defLen: Int, encoding: String? = packetEncoding): String? {
        var fixLen = ""
        try {
            if (valueStr == null) {
                return strCopy("0", defLen)
            } else {
                val valueStrByte = if (encoding == null || encoding.trim() == "") {
                    valueStr.toByteArray()
                } else {
                    valueStr.toByteArray(charset(encoding))
                }
                //这里最好指定编码，不使用平台默认编码
                //长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
                //解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码
                if (valueStrByte.size > 10 * defLen) {
                    return null
                } else {
                    val len = valueStrByte.size//字段实际长度
                    val len1 = len.toString()
                    fixLen = strCopy("0", defLen - len1.length) + len1
                }
            }
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message, e)
        }

        return fixLen
    }

    private fun getFixFieldValue(valueStr: String?, defLen: Int, encoding: String? = packetEncoding): String? {
        var fixLen = ""
        try {
            if (valueStr == null) {
                return strCopy(" ", defLen)
            } else {
                val valueStrByte = if (encoding == null || encoding.trim() == "") {
                    valueStr.toByteArray()
                } else {
                    valueStr.toByteArray(charset(encoding))
                }
                //这里最好指定编码，不使用平台默认编码
                //长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
                //解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码
                if (valueStrByte.size > defLen) {
                    return null
                } else {
                    fixLen = valueStr + strCopy(" ", defLen - valueStrByte.size)
                }
            }
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message, e)
        }

        return fixLen
    }

}