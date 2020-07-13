package pers.acp.ftp.user

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
abstract class ServerUser {

    /**
     * 用户名
     */
    var username: String? = null

    /**
     * 密码
     */
    var password: String? = null

    /**
     * 可访问路径
     */
    var homeDirectory = "/"

    /**
     * 是否启用
     */
    var isEnableFlag = true

}
