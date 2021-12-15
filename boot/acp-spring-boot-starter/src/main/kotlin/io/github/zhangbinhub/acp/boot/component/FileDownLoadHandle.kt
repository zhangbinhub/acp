package io.github.zhangbinhub.acp.boot.component

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.*
import java.net.URLEncoder

/**
 * 文件下载处理组件
 *
 * @author zhangbin by 2018-1-21 1:30
 * @since JDK 11
 */
class FileDownLoadHandle(private val logAdapter: LogAdapter) {

    @Throws(ServerException::class)
    @JvmOverloads
    fun downLoadForWeb(
        request: HttpServletRequest,
        response: HttpServletResponse,
        path: String,
        allowPathRegexList: List<String>? = null,
        isDelete: Boolean = false,
        deleteWaitTime: Long? = null
    ) {
        var filePath = path.replace("/", File.separator).replace("\\", File.separator)
        if (!filePath.startsWith(File.separator)) {
            filePath = File.separator + filePath
        }
        val webRootPath = CommonTools.getWebRootAbsPath()
        if (webRootPath != File.separator) {
            filePath = webRootPath + filePath
        }
        downLoadFile(request, response, filePath, allowPathRegexList, isDelete, deleteWaitTime)
    }

    @Throws(ServerException::class)
    @JvmOverloads
    fun downLoadFile(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filePath: String,
        allowPathRegexList: List<String>? = null,
        isDelete: Boolean = false,
        deleteWaitTime: Long? = null
    ) {
        val path = filePath.replace("/", File.separator).replace("\\", File.separator)
        val filterRegex: MutableList<String> = mutableListOf()
        if (allowPathRegexList == null || allowPathRegexList.isEmpty()) {
            filterRegex.addAll(
                mutableListOf(
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}tmp${File.separator}.*",
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}upload${File.separator}.*",
                    CommonTools.getWebRootAbsPath() + "${File.separator}files${File.separator}download${File.separator}.*"
                )
            )
        } else {
            filterRegex.addAll(allowPathRegexList)
        }
        val file = File(path)
        if (pathFilter(filterRegex, file.canonicalPath)) {
            var fis: RandomAccessFile? = null
            var toClient: OutputStream? = null
            try {
                if (!file.exists()) {
                    throw ServerException("the file [$filePath] is not exists")
                }
                val filename = file.name
                response.reset()
                response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                response.addHeader(
                    "Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(filename, request.characterEncoding)
                )
                // 解析断点续传相关信息
                response.setHeader("Accept-Ranges", "bytes")
                val downloadSize = file.length()
                var fromPos: Long = 0
                var toPos: Long = 0
                if (request.getHeader("Range") == null) {
                    response.setContentLengthLong(downloadSize)
                } else {
                    response.status = HttpStatus.PARTIAL_CONTENT.value()
                    val bytes = request.getHeader("Range").replace("bytes=", "")
                    val ary = bytes.split("-")
                    fromPos = ary[0].toLong()
                    if (ary.size == 2) {
                        toPos = ary[1].toLong()
                    }
                    val size = if (toPos > fromPos) {
                        toPos - fromPos + 1
                    } else {
                        toPos = downloadSize - 1
                        downloadSize - fromPos
                    }
                    response.addHeader("Content-Range", "bytes ${fromPos}-${toPos}/${downloadSize}")
                    response.setContentLengthLong(size)
                }
                toClient = BufferedOutputStream(response.outputStream)
                fis = RandomAccessFile(file, "r")
                if (fromPos > 0) {
                    fis.seek(fromPos)
                }
                val buffer = ByteArray(2048)
                var count = 0 // 当前写到客户端的大小
                var i = fis.read(buffer)
                while (i != -1) {
                    toClient.write(buffer, 0, i)
                    count += i
                    i = fis.read(buffer)
                }
                fis.close()
                toClient.flush()
                toClient.close()
                logAdapter.debug("download file Success:$filename")
                if (isDelete) {
                    CommonTools.doDeleteFile(file, true, deleteWaitTime)
                }
            } catch (e: Exception) {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (ex: IOException) {
                        logAdapter.error(ex.message, ex)
                    }

                }
                if (toClient != null) {
                    try {
                        toClient.close()
                    } catch (ex: IOException) {
                        logAdapter.error(ex.message, ex)
                    }

                }
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

        } else {
            throw ServerException("download file failed,the file path is not correct")
        }
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

}
