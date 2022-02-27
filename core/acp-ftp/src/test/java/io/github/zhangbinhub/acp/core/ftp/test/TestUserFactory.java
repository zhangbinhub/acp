package io.github.zhangbinhub.acp.core.ftp.test;

import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.ftp.server.FtpServerUser;
import io.github.zhangbinhub.acp.core.ftp.server.SftpServerUser;
import io.github.zhangbinhub.acp.core.ftp.user.UserFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangbin by 28/09/2018 16:36
 * @since JDK 11
 */
public class TestUserFactory implements UserFactory {

    @NotNull
    @Override
    public List<FtpServerUser> generateFtpUserList() {
        List<FtpServerUser> userList = new ArrayList<>();
        FtpServerUser ftpServerUser = new FtpServerUser();
        ftpServerUser.setUsername("zb");
        ftpServerUser.setPassword("1");
        ftpServerUser.setEnableFlag(true);
        ftpServerUser.setWritePermission(true);
        userList.add(ftpServerUser);
        return userList;
    }

    @NotNull
    @Override
    public List<SftpServerUser> generateSFtpUserList() {
        List<SftpServerUser> userList = new ArrayList<>();
        SftpServerUser sftpServerUser = new SftpServerUser();
        sftpServerUser.setUsername("zhang");
        sftpServerUser.setPassword("1");
        //zb
//        sftpServerUser.setPublicKey("AAAAB3NzaC1yc2EAAAADAQABAAABAQDOkYRFzDTr0fk/ZlgOCIDp0p7a/MiK8s7utvw+qJ5n/gFys9QkNjMT1cQaZu5bbVlHbrGqXMAo4Hu53C1nQRPG1Db8WISYbsU5SbMF2hej1GZkGthUZOk8iAv+JDL72unlZGft75U6oJhttLVOn36I3rDcDSzUYc6nXk+Jihn5K2pTiwSKLeAdL4cYXBJwQIplKfmMVUpY+luCgSQhCUp1SGkSr8sK5M54RWLSwrAxli1Em/BjqM1c9cWnLXKxlijMUbUaCQedmrrzVdrv2MU5hIgiG2N4dAm44H6IF86h8scIHiXRgmH0VS9MLHPH9d/ggoja8BTUPeGWEGoUH0E5");
//        sftpServerUser.setPublicKey("AAAAB3NzaC1kc3MAAACBAO+hNa2prmRSPQ9CVuQM2J+enmOiRDcbHIB+exrFXVQBblzAkOmgHnGDfxtRqFjCwNABdtUShWTlTaSLqkw5AWGoWP78bJn9U5hVTb1TEyHloWO5nLZQ1rzenJUvOxjV5e2r2PFpHlwIllZV33wA2HzWq0cFD1Bi17+xfW5YTXfZAAAAFQDAQP9EdMv6GOdhEf04A4j3Hsh4PQAAAIEA6vQLvS55l2XehhDUm7krZ31zI9oPZUml1miuPwtIsuxCObuiER3EvhdSQ59ff4gajfkw+fKqPm+mQ+Ma/i0glatFctwJSGWZENF+O1KjSAcLmdNu6IS5mhNS4zmpj4GS6fOs6VRX1rdH3RgDbB/M1HSJjya1md0bFTZVS5kUwA8AAACABO/C9Phm7ONbDWd+evbeTjHQ9Zal3xq+Qe4Os+EiGW3bfoPCdtei5P6jJbCIdYHfu/mR+GYLW+L/HsXiqrnuW4RnCY7OoXyCX+SeK2jtdPkrfcHIv0tiC65FWZjx274RlYUiJgSfE+tcszUsR6H6pYOwxmN2yRmKla2fSIsCsvc=");
        //yjy
//        sftpServerUser.setPublicKey("AAAAB3NzaC1yc2EAAAADAQABAAABAQCrEkxHw/rCPEB/Xh8vfchtxXIyLnGDnAOXFnIwRLXt+WS9yyir8zi/XJJhXC7zFa5eUHQM+Ji+PFYDEL4tIlhnAWPzt+W6q5Ov69TeBg6pNkSWq//Shvc7NqYn7yjp3aJQHbtI+Zp7fu+kSvcDR+lszI0M/eHLZ0P6J9DHLk54oBGfWWY5KHMr/PHnG3hiP6NbObyBRXB28XEFsCY3bhiLTjHnnGfw4B4Z3DBALX6SDRxyJUFBRaZ9YXr0hlbPOPhkUqZ+IYmu7eHdgObMSIKgOXj0YRHrKtcZ+NXdqDaaBPKnydpJzycniYvmEcvx62mdsgKO9eGyesaG8Q+kxIcp");
        sftpServerUser.setPublicKey(CommonTools.getAbsPath("/files/resource/userpkey/rsa_public_key.pem"));
        sftpServerUser.setEnableFlag(true);
        userList.add(sftpServerUser);
        return userList;
    }

}
