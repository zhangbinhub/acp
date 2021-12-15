package com.github.zhangbinhub.acp.cloud.server.hello;

import org.springframework.boot.SpringApplication;
import com.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 2018-3-5 13:56
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class HelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

}
