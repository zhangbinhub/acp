package com.github.zhangbinhub.acp.core.ftp.server

import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator
import org.apache.sshd.server.session.ServerSession
import com.github.zhangbinhub.acp.core.log.LogFactory
import com.github.zhangbinhub.acp.core.security.key.KeyManagement
import java.security.PublicKey

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
internal class UserPublicKeyAuthenticator(private val userList: List<SftpServerUser>, private val needAuth: Boolean, private val keyAuthMode: String, private val keyAuthType: String) : PublickeyAuthenticator {

    private val log = LogFactory.getInstance(this.javaClass)

    override fun authenticate(username: String, publicKey: PublicKey, serverSession: ServerSession): Boolean {
        var result = false
        if (needAuth) {
            var isExist = false
            for (sftpServerUser in userList) {
                if (sftpServerUser.isEnableFlag) {
                    if (sftpServerUser.username == username) {
                        isExist = true
                        try {
                            val userPublicKey = getUserPublicKey(sftpServerUser.publicKey)
                            if (publicKey == userPublicKey) {
                                result = true
                                log.info("sftp user [$username] certificate authentication successFull")
                            } else {
                                result = false
                                log.error("sftp user [$username] certificate authentication failed : certificate is invalid")
                            }
                        } catch (e: Exception) {
                            log.error("certificate authentication exception : " + e.message, e)
                            result = false
                        }

                        break
                    }
                }
            }
            if (!isExist) {
                log.error("sftp user [$username] certificate authentication failed : user is not existence")
            }
        } else {
            log.error("sftp server certificate authentication is not available")
        }
        return result
    }

    @Throws(Exception::class)
    private fun getUserPublicKey(publicKey: String?): PublicKey? =
            when (keyAuthType) {
                "der" -> if (keyAuthMode == "DSA") {
                    KeyManagement.getDSAPublicKeyForDER(publicKey!!)
                } else {
                    KeyManagement.getRSAPublicKeyForDER(publicKey!!)
                }
                "pem" -> if (keyAuthMode == "DSA") {
                    KeyManagement.getDSAPublicKeyForPEM(publicKey!!)
                } else {
                    KeyManagement.getRSAPublicKeyForPEM(publicKey!!)
                }
                "ssh" -> if (keyAuthMode == "DSA") {
                    KeyManagement.getDSAPublicKeyForSSH(publicKey!!)
                } else {
                    KeyManagement.getRSAPublicKeyForSSH(publicKey!!)
                }
                else -> null
            }

}