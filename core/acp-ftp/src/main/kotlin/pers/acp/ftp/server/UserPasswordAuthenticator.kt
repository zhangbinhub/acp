package pers.acp.ftp.server

import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.session.ServerSession
import pers.acp.core.log.LogFactory

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
internal class UserPasswordAuthenticator(private val userList: List<SftpServerUser>, private val needAuth: Boolean) : PasswordAuthenticator {

    private val log = LogFactory.getInstance(this.javaClass)

    override fun authenticate(username: String, password: String, serverSession: ServerSession): Boolean {
        var result = false
        if (needAuth) {
            var isExist = false
            for (sftpServerUser in userList) {
                if (sftpServerUser.isEnableFlag) {
                    if (sftpServerUser.username == username) {
                        isExist = true
                        if (sftpServerUser.password == password) {
                            result = true
                            log.info("sftp user [$username] password authentication successFull")
                            break
                        } else {
                            log.error("sftp user [$username] password authentication failed : password error")
                        }
                    }
                }
            }
            if (!isExist) {
                log.error("sftp user [$username] password authentication failed : user is not existence")
            }
        } else {
            log.error("sftp server password authentication is not available")
        }
        return result
    }

}