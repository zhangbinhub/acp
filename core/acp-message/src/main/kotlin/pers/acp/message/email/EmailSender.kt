package pers.acp.message.email

import pers.acp.message.exceptions.EmailException
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.Message.RecipientType
import javax.mail.internet.*
import java.util.ArrayList
import java.util.Properties

/**
 * @author zhang by 11/07/2019
 * @since JDK 11
 */
class EmailSender(private val emailEntity: EmailEntity) {

    private val log = LogFactory.getInstance(this.javaClass)

    /**
     * 邮件发送
     *
     * @param charset 内容字符集，默认系统字符集
     * @return 成功或失败
     */
    @JvmOverloads
    fun doSend(charset: String = CommonTools.getDefaultCharset()): Boolean {
        try {
            val props = Properties()
            if (emailEntity.ssl) {
                val sslFactory = "javax.net.ssl.SSLSocketFactory"
                props.setProperty("mail.smtp.socketFactory.class", sslFactory)
                props.setProperty("mail.smtp.socketFactory.fallback", "false")
                props.setProperty("mail.smtp.socketFactory.port", emailEntity.mailPort.toString())
                props.setProperty("mail.smtp.starttls.enable", "true")
                props.setProperty("mail.smtp.ssl.enable", "true")
            }
            props.setProperty("mail.smtp.host", emailEntity.mailHost)
            props.setProperty("mail.smtp.port", emailEntity.mailPort.toString())
            props.setProperty("mail.transport.protocol", emailEntity.mailTransportProtocol)
            props.setProperty("mail.smtp.auth", emailEntity.mailSmtpAuth.toString())
            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(emailEntity.userName, emailEntity.password)
                }
            })
            session.debug = emailEntity.deBug
            /* 创建邮件 */
            val message = createMail(session, charset)
            /* 发送邮件 */
            Transport.send(message, message.allRecipients)
            return true
        } catch (e: Exception) {
            log.error(e.message, e)
            return false
        }
    }

    /**
     * 创建邮件
     *
     * @param session 回话
     * @return 邮件实例
     */
    @Throws(Exception::class)
    private fun createMail(session: Session, charset: String): MimeMessage {
        val message = MimeMessage(session)
        if (CommonTools.isNullStr(emailEntity.senderAddress)) {
            throw EmailException("senderAddress is null or empty")
        }
        message.setFrom(InternetAddress(emailEntity.senderAddress!!))
        if (emailEntity.recipientAddresses == null || emailEntity.recipientAddresses!!.isEmpty()) {
            throw EmailException("recipientAddresses is null or empty")
        }
        val addresses = ArrayList<InternetAddress>()
        for (recipientAddress in emailEntity.recipientAddresses!!) {
            if (!CommonTools.isNullStr(recipientAddress)) {
                addresses.add(InternetAddress(recipientAddress))
            }
        }
        message.addRecipients(RecipientType.TO, addresses.toTypedArray())
        if (emailEntity.recipientCCAddresses != null && emailEntity.recipientCCAddresses!!.isNotEmpty()) {
            val addressesCC = ArrayList<InternetAddress>()
            for (recipientAddress in emailEntity.recipientCCAddresses!!) {
                if (!CommonTools.isNullStr(recipientAddress)) {
                    addressesCC.add(InternetAddress(recipientAddress))
                }
            }
            message.addRecipients(RecipientType.CC, addressesCC.toTypedArray())
        }
        if (emailEntity.recipientBCCAddresses != null && emailEntity.recipientBCCAddresses!!.isNotEmpty()) {
            val addressesBCC = ArrayList<InternetAddress>()
            for (recipientAddress in emailEntity.recipientBCCAddresses!!) {
                if (!CommonTools.isNullStr(recipientAddress)) {
                    addressesBCC.add(InternetAddress(recipientAddress))
                }
            }
            message.addRecipients(RecipientType.BCC, addressesBCC.toTypedArray())
        }
        message.subject = emailEntity.mailSubject
        message.sentDate = CommonTools.getNowDateTime().toDate()

        /* 正文主体 */
        val content = MimeBodyPart()

        /* 正文文本 */
        val text = MimeBodyPart()
        text.setContent(emailEntity.content, "text/html;charset=$charset")
        val mmImage = MimeMultipart()
        mmImage.addBodyPart(text)

        /* 图片 */
        if (emailEntity.images != null && emailEntity.images!!.isNotEmpty()) {
            /* 图片内容 */
            for ((cid, value) in emailEntity.images!!) {
                val imagePath = CommonTools.getAbsPath(value)
                val image = MimeBodyPart()
                val dh = DataHandler(FileDataSource(imagePath))
                image.dataHandler = dh
                image.contentID = cid
                mmImage.addBodyPart(image)
            }
            mmImage.setSubType("related")
        }
        content.setContent(mmImage)

        /* 附件 */
        if (emailEntity.attaches != null && emailEntity.attaches!!.isNotEmpty()) {
            val mmAttache = MimeMultipart()
            /* 附件内容 */
            for (attachePath in emailEntity.attaches!!) {
                val attach = MimeBodyPart()
                val dh = DataHandler(FileDataSource(attachePath))
                attach.dataHandler = dh
                attach.fileName = MimeUtility.encodeText(dh.name, charset, null)
                mmAttache.addBodyPart(attach)
            }
            mmAttache.setSubType("mixed")
            mmAttache.addBodyPart(content)
            message.setContent(mmAttache)
            message.saveChanges()
        } else {
            message.setContent(mmImage)
            message.saveChanges()
        }
        return message
    }

}