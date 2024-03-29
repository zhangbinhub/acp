package io.github.zhangbinhub.acp.cloud.server.hello;

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication;
import org.springframework.boot.SpringApplication;

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
