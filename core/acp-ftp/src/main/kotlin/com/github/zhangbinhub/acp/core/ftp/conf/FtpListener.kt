package com.github.zhangbinhub.acp.core.ftp.conf

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class FtpListener {

    @XStreamAsAttribute
    @XStreamAlias("name")
    var name: String? = null

    @XStreamAsAttribute
    @XStreamAlias("enabled")
    var enabled = false

    @XStreamAsAttribute
    @XStreamAlias("port")
    var port: Int = 0

    @XStreamAsAttribute
    @XStreamAlias("anonymousLoginEnabled")
    var anonymousLoginEnabled = false

    @XStreamAsAttribute
    @XStreamAlias("pwdEncryptMode")
    var pwdEncryptMode = "MD5"

    @XStreamAsAttribute
    @XStreamAlias("loginFailureDelay")
    var loginFailureDelay = 30

    @XStreamAsAttribute
    @XStreamAlias("maxLoginFailures")
    var maxLoginFailures = 20

    @XStreamAsAttribute
    @XStreamAlias("maxLogins")
    var maxLogins = 10

    @XStreamAsAttribute
    @XStreamAlias("maxAnonymousLogins")
    var maxAnonymousLogins = 20

    @XStreamAsAttribute
    @XStreamAlias("maxThreads")
    var maxThreads = 10

    @XStreamAsAttribute
    @XStreamAlias("defaultHomeDirectory")
    var defaultHomeDirectory: String? = null

    @XStreamAsAttribute
    @XStreamAlias("anonymousWritePermission")
    var anonymousWritePermission = false

    @XStreamAsAttribute
    @XStreamAlias("userFactoryClass")
    var userFactoryClass: String? = null

}
