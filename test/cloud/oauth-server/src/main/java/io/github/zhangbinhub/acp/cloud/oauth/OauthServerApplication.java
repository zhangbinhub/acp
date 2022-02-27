package io.github.zhangbinhub.acp.cloud.oauth;

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudOauthServerApplication;
import org.springframework.boot.SpringApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudOauthServerApplication
public class OauthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthServerApplication.class, args);
    }

}
