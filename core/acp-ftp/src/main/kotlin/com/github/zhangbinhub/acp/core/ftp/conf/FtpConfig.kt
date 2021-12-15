package com.github.zhangbinhub.acp.core.ftp.conf

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.github.zhangbinhub.acp.core.base.BaseXml
import com.github.zhangbinhub.acp.core.log.LogFactory

/**
 * @author zhang by 12/07/2019
 * @since JDK 11
 */
@XStreamAlias("ftp-config")
class FtpConfig : BaseXml() {

    @XStreamImplicit(itemFieldName = "listen")
    var listens: List<FtpListener>? = null

    companion object {

        private val log = LogFactory.getInstance(FtpConfig::class.java)

        @JvmStatic
        fun getInstance(): FtpConfig? =
            try {
                load(FtpConfig::class.java) as FtpConfig
            } catch (e: Exception) {
                log.error(e.message, e)
                null
            }
    }

}