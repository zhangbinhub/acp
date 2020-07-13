package pers.acp.spring.boot.ftp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.ftp.InitFtpServer
import pers.acp.ftp.InitSftpServer
import pers.acp.ftp.user.UserFactory
import pers.acp.spring.boot.ftp.conf.SftpServerConfiguration
import pers.acp.spring.boot.ftp.conf.FtpServerConfiguration
import pers.acp.spring.boot.ftp.init.FtpServerInitialization
import pers.acp.spring.boot.ftp.init.SftpServerInitialization

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        FtpServerConfiguration::class,
        SftpServerConfiguration::class)
class AcpFtpAutoConfiguration {

    @Bean
    @ConditionalOnClass(InitFtpServer::class)
    @Autowired(required = false)
    fun ftpServerInitialization(ftpServerConfiguration: FtpServerConfiguration,
                                userFactoryList: List<UserFactory>?) = FtpServerInitialization(ftpServerConfiguration, userFactoryList)

    @Bean
    @ConditionalOnClass(InitSftpServer::class)
    @Autowired(required = false)
    fun sftpServerInitialization(sftpServerConfiguration: SftpServerConfiguration,
                                 userFactoryList: List<UserFactory>?) = SftpServerInitialization(sftpServerConfiguration, userFactoryList)

}
