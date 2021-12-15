package com.github.zhangbinhub.acp.cloud.oauth;

import org.springframework.boot.SpringApplication;
import com.github.zhangbinhub.acp.cloud.annotation.AcpCloudOauthServerApplication;

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
