package com.github.zhangbinhub.acp.core.base

import com.github.zhangbinhub.acp.core.exceptions.ConfigException
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.tools.CommonUtils
import java.io.*
import java.util.concurrent.ConcurrentHashMap
import java.lang.IllegalAccessException
import java.lang.NoSuchMethodException
import java.lang.reflect.InvocationTargetException
import java.lang.Exception
import java.util.*

/**
 * Create by zhangbin on 2017-8-7 17:49
 */
abstract class BaseProperties : Properties() {
    private var fileAbsPathName: String = ""
    var comments: String? = null
    private var lastModified: Long = 0

    @Throws(ConfigException::class)
    fun storeToProperties() {
        var oFile: OutputStreamWriter? = null
        try {
            if (!CommonUtils.isNullStr(fileAbsPathName)) {
                synchronized(this) {
                    val file = File(fileAbsPathName)
                    if (!file.parentFile.exists()) {
                        if (!file.parentFile.mkdirs()) {
                            log.error("mkdirs failed : " + file.parentFile.canonicalPath)
                        }
                    }
                    if (!file.exists() && !file.createNewFile()) {
                        throw ConfigException("create file failed : $fileAbsPathName")
                    }
                    oFile = OutputStreamWriter(FileOutputStream(file), CommonUtils.defaultCharset)
                    this.store(oFile, comments)
                }
                log.info("write [" + fileAbsPathName + "] success => " + this.javaClass.canonicalName)
            } else {
                throw ConfigException("write config failed: need specify properties file for " + this.javaClass.canonicalName)
            }
        } catch (e: Exception) {
            log.error(e.message, e)
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

    fun setFileAbsPathName(fileAdbPathName: String) {
        this.fileAbsPathName = fileAdbPathName
    }

    companion object {
        private const val serialVersionUID = -8039022312482420408L
        private val log = LogFactory.getInstance(BaseProperties::class.java) // 日志对象
        private val instanceMap: MutableMap<String, BaseProperties?> = ConcurrentHashMap()

        @Throws(
            ConfigException::class,
            IOException::class,
            IllegalAccessException::class,
            InstantiationException::class,
            NoSuchMethodException::class,
            InvocationTargetException::class
        )
        fun getInstance(cls: Class<out BaseProperties?>, propertiesFileName: String): BaseProperties? {
            return getInstance(cls, propertiesFileName, null)
        }

        @JvmStatic
        @Throws(
            ConfigException::class,
            IOException::class,
            IllegalAccessException::class,
            InstantiationException::class,
            NoSuchMethodException::class,
            InvocationTargetException::class
        )
        fun getInstance(
            cls: Class<out BaseProperties?>,
            propertiesFileName: String,
            fileAbsPathName: String?
        ): BaseProperties? {
            var absPathName = fileAbsPathName
            return if (!CommonUtils.isNullStr(propertiesFileName)) {
                absPathName = if (CommonUtils.isNullStr(absPathName)) {
                    CommonUtils.getAbsPath(propertiesFileName)
                } else {
                    absPathName!!.replace("/", File.separator).replace("\\", File.separator)
                }
                val file = File(absPathName)
                var inputStreamReader: InputStreamReader? = null
                if (!file.exists()) {
                    val input = CommonUtils.getResourceInputStream(propertiesFileName.replace("\\", "/"))
                    if (input != null) {
                        inputStreamReader = InputStreamReader(input, CommonUtils.defaultCharset)
                    } else {
                        log.warn("$propertiesFileName is not found")
                    }
                } else {
                    inputStreamReader =
                        InputStreamReader(FileInputStream(file), CommonUtils.defaultCharset)
                }
                var instance = instanceMap[propertiesFileName]
                if (!instanceMap.containsKey(propertiesFileName) || file.exists() && file.lastModified() > instance!!.lastModified) {
                    if (inputStreamReader != null) {
                        synchronized(instanceMap) {
                            instance = cls.getDeclaredConstructor().newInstance()
                            instance!!.load(inputStreamReader)
                            if (file.exists()) {
                                instance!!.lastModified = file.lastModified()
                            }
                            instance!!.fileAbsPathName = absPathName
                            instanceMap[propertiesFileName] = instance
                            log.info("load [" + propertiesFileName + "] success => " + cls.canonicalName)
                            return instance
                        }
                    } else {
                        null
                    }
                } else {
                    instance
                }
            } else {
                throw ConfigException("load config failed : need specify properties file for [" + cls.canonicalName + "]")
            }
        }
    }
}