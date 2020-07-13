package pers.acp.ftp.test;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;
import pers.acp.core.security.Md5Encrypt;
import pers.acp.ftp.client.FtpClient;
import pers.acp.ftp.client.FtpConnectMode;
import pers.acp.ftp.client.SftpClient;

import java.util.List;

/**
 * @author zhang by 05/03/2019
 * @since JDK 11
 */
public class TestClient {

    @Test
    void testSftp() {
        SftpClient sftpClient = new SftpClient("127.0.0.1", 4221, "ftp", "1");
        List<ChannelSftp.LsEntry> sftpList = sftpClient.getFileEntityListForSFTP();
        System.out.println("----------------------sftp-----------------------");
        for (ChannelSftp.LsEntry entity : sftpList) {
            System.out.println(entity.getFilename() + ", is dir = " + entity.getAttrs().isDir());
            System.out.println(entity.getAttrs().isReg());
            System.out.println("-----------------------");
        }
    }

    @Test
    void testFtp() {
        FtpClient ftpClient = new FtpClient("localhost", 221, "ftp", "1");
//        ftpClient.setRemotePath("logs/testkotlin");
        List<FTPFile> ftpList = ftpClient.getFileEntityListForFTP();
        System.out.println("----------------------ftp-----------------------");
        for (FTPFile file : ftpList) {
            System.out.println(file.getName());
            System.out.println(file.isDirectory());
            System.out.println(file.isFile());
            System.out.println("-----------------------");
        }
    }

}
