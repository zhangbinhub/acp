package io.github.zhangbinhub.acp.core.message.email

import org.apache.commons.lang3.StringUtils
import io.github.zhangbinhub.acp.core.CommonTools

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
data class EmailEntity(
        /**
         * 设置发送者邮箱地址
         */
        var senderAddress: String? = null,

        /**
         * 设置邮件服务器登录密码
         */
        var password: String? = null,

        /**
         * 设置邮件服务器端口
         */
        var mailPort: Int = 465,

        /**
         * 设置邮件协议，默认"smpt"
         */
        var mailTransportProtocol: String = "smtp",

        /**
         * 是否进行smtp协议认证，默认true
         */
        var mailSmtpAuth: Boolean = true,

        /**
         * 是否输出调试信息
         */
        var deBug: Boolean = false,

        /**
         * 是否采用https安全链接
         */
        var ssl: Boolean = true,

        /**
         * 设置邮件接收者地址
         */
        var recipientAddresses: List<String>? = null,

        /**
         * 设置邮件抄送人地址，为null则不抄送，默认为null
         */
        var recipientCCAddresses: List<String>? = null,

        /**
         * 设置邮件密送人地址，为null则不密送，默认为null
         */
        var recipientBCCAddresses: List<String>? = null,

        /**
         * 设置邮件标题
         */
        var mailSubject: String? = null,

        /**
         * 设置邮件正文文本
         */
        var content: String = "",

        /**
         * 设置邮件图片 Map: key:ContentID,value:图片路径（绝对路径或相对于webroot以"/"开头的路径）
         */
        var images: Map<String, String>? = null,

        /**
         * 附件文件路径：绝对路径或相对于webroot以"/"开头的路径
         */
        var attaches: List<String>? = null
) {

    /**
     * 获取邮件服务器用户名，默认从发送者邮箱地址截取
     *
     * @return 用户名
     */
    var userName: String = ""
        get() {
            if (CommonTools.isNullStr(field)) {
                if (!CommonTools.isNullStr(senderAddress)) {
                    val senderInfo = StringUtils.splitPreserveAllTokens(senderAddress, "@")
                    if (senderInfo.size == 2) {
                        field = senderInfo[0]
                    }
                }
            }
            return field
        }

    /**
     * 获取邮件服务器主机名，默认从发送者邮箱地址截取
     *
     * @return 邮件服务器主机名
     */
    var mailHost: String = ""
        get() {
            if (CommonTools.isNullStr(field)) {
                if (!CommonTools.isNullStr(senderAddress)) {
                    val senderInfo = StringUtils.splitPreserveAllTokens(senderAddress, "@")
                    if (senderInfo.size == 2) {
                        field = "smtp." + senderInfo[1]
                    }
                }
            }
            return field
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmailEntity

        if (senderAddress != other.senderAddress) return false
        if (password != other.password) return false
        if (mailPort != other.mailPort) return false
        if (mailTransportProtocol != other.mailTransportProtocol) return false
        if (mailSmtpAuth != other.mailSmtpAuth) return false
        if (deBug != other.deBug) return false
        if (ssl != other.ssl) return false
        if (recipientAddresses != other.recipientAddresses) return false
        if (recipientCCAddresses != other.recipientCCAddresses) return false
        if (recipientBCCAddresses != other.recipientBCCAddresses) return false
        if (mailSubject != other.mailSubject) return false
        if (content != other.content) return false
        if (images != other.images) return false
        if (attaches != other.attaches) return false

        return true
    }

    override fun hashCode(): Int {
        var result = senderAddress?.hashCode() ?: 0
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + mailPort
        result = 31 * result + mailTransportProtocol.hashCode()
        result = 31 * result + mailSmtpAuth.hashCode()
        result = 31 * result + deBug.hashCode()
        result = 31 * result + ssl.hashCode()
        result = 31 * result + (recipientAddresses?.hashCode() ?: 0)
        result = 31 * result + (recipientCCAddresses?.hashCode() ?: 0)
        result = 31 * result + (recipientBCCAddresses?.hashCode() ?: 0)
        result = 31 * result + (mailSubject?.hashCode() ?: 0)
        result = 31 * result + content.hashCode()
        result = 31 * result + (images?.hashCode() ?: 0)
        result = 31 * result + (attaches?.hashCode() ?: 0)
        return result
    }

}
