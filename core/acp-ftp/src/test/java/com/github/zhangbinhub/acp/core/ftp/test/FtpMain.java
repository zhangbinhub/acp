package com.github.zhangbinhub.acp.core.ftp.test;

import com.github.zhangbinhub.acp.core.ftp.InitFtpServer;
import com.github.zhangbinhub.acp.core.ftp.InitSftpServer;
import com.github.zhangbinhub.acp.core.ftp.InitFtpServer;
import com.github.zhangbinhub.acp.core.ftp.InitSftpServer;

/**
 * @author zhangbin by 28/09/2018 16:36
 * @since JDK 11
 */
public class FtpMain {

    public static void main(String[] args) {
        InitFtpServer.startFtpServer();
        InitSftpServer.startSftpServer();
    }

}
