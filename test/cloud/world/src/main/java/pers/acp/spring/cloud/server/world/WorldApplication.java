package pers.acp.spring.cloud.server.world;

import org.springframework.boot.SpringApplication;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 2018-3-5 13:56
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class WorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorldApplication.class, args);
    }

}
