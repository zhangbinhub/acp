package io.github.zhangbinhub.acp.core.ftp.conf

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
class SftpListener {

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
    @XStreamAlias("hostKeyPath")
    var hostKeyPath: String? = null

    @XStreamAsAttribute
    @XStreamAlias("passwordAuth")
    var passwordAuth = true

    @XStreamAsAttribute
    @XStreamAlias("publicKeyAuth")
    var publicKeyAuth = false

    @XStreamAsAttribute
    @XStreamAlias("keyAuthType")
    var keyAuthType = "pem"

    @XStreamAsAttribute
    @XStreamAlias("keyAuthMode")
    var keyAuthMode = "RSA"

    @XStreamAsAttribute
    @XStreamAlias("defaultHomeDirectory")
    var defaultHomeDirectory: String? = null

    @XStreamAsAttribute
    @XStreamAlias("userFactoryClass")
    var userFactoryClass: String? = null

}
