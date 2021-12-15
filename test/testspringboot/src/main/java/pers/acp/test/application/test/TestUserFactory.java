package pers.acp.test.application.test;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import com.github.zhangbinhub.acp.core.ftp.server.FtpServerUser;
import com.github.zhangbinhub.acp.core.ftp.server.SftpServerUser;
import com.github.zhangbinhub.acp.core.ftp.user.UserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 21/06/2019
 * @since JDK 11
 */
@Component
public class TestUserFactory implements UserFactory {

    @NotNull
    @Override
    public List<FtpServerUser> generateFtpUserList() {
        List<FtpServerUser> result = new ArrayList<>();
        FtpServerUser ftpServerUser = new FtpServerUser();
        ftpServerUser.setUsername("ftp");
        ftpServerUser.setPassword("1");
        ftpServerUser.setWritePermission(true);
        ftpServerUser.setMaxLoginNumber(10);
        ftpServerUser.setMaxLoginPerIp(10);
        ftpServerUser.setHomeDirectory("/");
        ftpServerUser.setEnableFlag(true);
        result.add(ftpServerUser);
        return result;
    }

    @NotNull
    @Override
    public List<SftpServerUser> generateSFtpUserList() {
        List<SftpServerUser> result = new ArrayList<>();
        SftpServerUser sftpServerUser = new SftpServerUser();
        sftpServerUser.setUsername("ftp");
        sftpServerUser.setPassword("1");
        sftpServerUser.setHomeDirectory("/");
        sftpServerUser.setEnableFlag(true);
        result.add(sftpServerUser);
        return result;
    }

}
