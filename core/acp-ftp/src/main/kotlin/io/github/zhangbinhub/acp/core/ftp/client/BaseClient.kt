package io.github.zhangbinhub.acp.core.ftp.client

import io.github.zhangbinhub.acp.core.CommonTools

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
abstract class BaseClient(
    /**
     * FTP 服务器地址IP地址
     */
    var hostname: String,
    /**
     * FTP 端口
     */
    var port: Int,
    /**
     * FTP 登录用户名
     */
    var username: String,
    /**
     * FTP 登录密码
     */
    var password: String
) {

    var charset = CommonTools.getDefaultCharset()

    var remotePath: String = ""

    var fileName: String = ""

    var localPath: String = ""

    protected fun formatRemotePath() {
        if (CommonTools.isNullStr(remotePath)) {
            remotePath = "/"
        } else {
            remotePath = remotePath.replace("\\", "/") + "/"
            if (remotePath.contains("../")) {
                remotePath = remotePath.substring(remotePath.lastIndexOf("../") + 3)
            }
            if (remotePath.contains("./")) {
                remotePath = remotePath.substring(remotePath.lastIndexOf("../") + 2)
            }
            if (!remotePath.startsWith("/")) {
                remotePath = "/$remotePath"
            }
        }
    }

    protected fun parseCurrAndSubFold(remotePath: String): Array<String> {
        val fold: String
        val subFold: String
        if (remotePath.startsWith("/")) {
            fold = ""
            subFold = remotePath.substring(1)
        } else {
            if (remotePath.contains("/")) {
                val index = remotePath.indexOf("/")
                fold = remotePath.substring(0, index)
                subFold = remotePath.substring(index + 1)
            } else {
                fold = remotePath
                subFold = ""
            }
        }
        return arrayOf(fold, subFold)
    }

}