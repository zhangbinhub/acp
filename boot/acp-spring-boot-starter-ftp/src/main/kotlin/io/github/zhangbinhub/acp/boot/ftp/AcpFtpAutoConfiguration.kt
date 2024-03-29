package io.github.zhangbinhub.acp.boot.ftp

import io.github.zhangbinhub.acp.boot.ftp.conf.FtpServerConfiguration
import io.github.zhangbinhub.acp.boot.ftp.conf.SftpServerConfiguration
import io.github.zhangbinhub.acp.boot.ftp.init.FtpServerInitialization
import io.github.zhangbinhub.acp.boot.ftp.init.SftpServerInitialization
import io.github.zhangbinhub.acp.core.ftp.InitFtpServer
import io.github.zhangbinhub.acp.core.ftp.InitSftpServer
import io.github.zhangbinhub.acp.core.ftp.user.UserFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
    FtpServerConfiguration::class,
    SftpServerConfiguration::class
)
class AcpFtpAutoConfiguration {

    @Bean
    @ConditionalOnClass(InitFtpServer::class)
    @Autowired(required = false)
    fun ftpServerInitialization(
        ftpServerConfiguration: FtpServerConfiguration,
        userFactoryList: List<UserFactory>?
    ) = FtpServerInitialization(ftpServerConfiguration, userFactoryList)

    @Bean
    @ConditionalOnClass(InitSftpServer::class)
    @Autowired(required = false)
    fun sftpServerInitialization(
        sftpServerConfiguration: SftpServerConfiguration,
        userFactoryList: List<UserFactory>?
    ) = SftpServerInitialization(sftpServerConfiguration, userFactoryList)

}
