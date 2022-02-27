package io.github.zhangbinhub.acp.core.base

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.security.AnyTypePermission
import io.github.zhangbinhub.acp.core.exceptions.ConfigException
import io.github.zhangbinhub.acp.core.log.LogFactory
import io.github.zhangbinhub.acp.core.tools.CommonUtils
import java.io.*
import java.util.concurrent.ConcurrentHashMap

abstract class BaseXml {
    private var fileAbsPathName: String = ""
    private var lastModified: Long = 0

    /**
     * 转换成 XML 字符串
     *
     * @return XML 字符串
     */
    @Throws(ConfigException::class)
    fun toXMLString(): String {
        val out = StringWriter()
        return try {
            synchronized(this) {
                val xStream = XStream(DomDriver())
                xStream.toXML(this, out)
            }
            out.toString()
        } catch (e: Exception) {
            log.error(e.message)
            throw ConfigException("convert config failed:[" + this.javaClass.canonicalName + "]")
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * 将java对象信息写入xml文件
     */
    @Throws(ConfigException::class)
    fun storeToXml() {
        var oFile: OutputStreamWriter? = null
        try {
            if (!CommonUtils.isNullStr(fileAbsPathName)) {
                synchronized(this) {
                    val file = File(fileAbsPathName)
                    val xStream = XStream(DomDriver())
                    oFile = OutputStreamWriter(FileOutputStream(file), CommonUtils.defaultCharset)
                    xStream.toXML(this, oFile)
                    lastModified = file.lastModified()
                }
                log.info("write [" + fileAbsPathName + "] success => " + this.javaClass.canonicalName)
            } else {
                throw ConfigException("write config failed: need specify XML file for " + this.javaClass.canonicalName)
            }
        } catch (e: Exception) {
            log.error(e.message)
            throw ConfigException("write config failed:[$fileAbsPathName]")
        } finally {
            if (oFile != null) {
                try {
                    oFile!!.close()
                } catch (e: IOException) {
                    log.error(e.message, e)
                }
            }
        }
    }

    fun getFileAbsPathName(): String {
        return fileAbsPathName
    }

    fun setFileAbsPathName(fileAbsPathName: String) {
        this.fileAbsPathName = fileAbsPathName
    }

    companion object {
        private val log = LogFactory.getInstance(BaseXml::class.java) // 日志对象
        private val instanceMap: MutableMap<String, BaseXml> = ConcurrentHashMap()

        /**
         * 序列化xml文件为java对象
         *
         * @param cls 序列化后转换的java类
         * @return 实例对象
         */
        @JvmStatic
        @Throws(ConfigException::class)
        protected fun load(cls: Class<out BaseXml?>): BaseXml? {
            var fileName: String? = null
            return try {
                fileName = CommonUtils.getProperties(cls.canonicalName, "")
                if (!CommonUtils.isNullStr(fileName)) {
                    val file = File(CommonUtils.getAbsPath(fileName))
                    var inputStreamReader: InputStreamReader? = null
                    if (!file.exists()) {
                        val input = CommonUtils.getResourceInputStream(fileName.replace("\\", "/"))
                        if (input != null) {
                            inputStreamReader = InputStreamReader(input, CommonUtils.defaultCharset)
                        } else {
                            log.error("$fileName is not found")
                        }
                    } else {
                        inputStreamReader = InputStreamReader(FileInputStream(file), CommonUtils.defaultCharset)
                    }
                    val instance = instanceMap[fileName]
                    if (!instanceMap.containsKey(fileName) || file.exists() && file.lastModified() > instance!!.lastModified) {
                        if (inputStreamReader != null) {
                            synchronized(instanceMap) {
                                val xStream = XStream(DomDriver())
                                xStream.addPermission(AnyTypePermission.ANY)
                                xStream.processAnnotations(cls)
                                val obj = xStream.fromXML(inputStreamReader) as BaseXml
                                obj.fileAbsPathName = CommonUtils.getAbsPath(fileName)
                                if (file.exists()) {
                                    obj.lastModified = file.lastModified()
                                }
                                instanceMap[fileName] = obj
                                log.info("load [" + fileName + "] success => " + cls.canonicalName)
                                return obj
                            }
                        } else {
                            null
                        }
                    } else {
                        instance
                    }
                } else {
                    log.warn("load config failed: need specify XML file for " + cls.canonicalName)
                    null
                }
            } catch (e: Exception) {
                log.error(e.message, e)
                throw ConfigException("load config failed:[$fileName]")
            }
        }
    }
}