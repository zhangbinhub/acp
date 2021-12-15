package com.github.zhangbinhub.acp.cloud.server.helloworld.hystrix;

import com.github.zhangbinhub.acp.cloud.server.helloworld.feign.WorldServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import com.github.zhangbinhub.acp.cloud.server.helloworld.feign.WorldServer;

/**
 * @author zhangbin by 2018-3-7 23:49
 * @since JDK 11
 */
@Component
public class WorldServerHystrix implements FallbackFactory<WorldServer> {

    private final LogAdapter log;

    @Autowired
    public WorldServerHystrix(LogAdapter log) {
        this.log = log;
    }

    @Override
    public WorldServer create(Throwable cause) {
        log.error("WorldServerHystrix hystrix caused by: " + cause.getMessage(), cause);
        return new WorldServer() {

            @Override
            public String fromClient(String name) {
                return "World service Hystrix[" + name + "]";
            }

            @Override
            public String fromClientTest() {
                return "World service Hystrix";
            }
        };
    }

}
