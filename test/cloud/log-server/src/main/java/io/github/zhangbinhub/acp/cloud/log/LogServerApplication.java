package io.github.zhangbinhub.acp.cloud.log;

import org.springframework.boot.SpringApplication;
import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class LogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogServerApplication.class, args);
    }

}
