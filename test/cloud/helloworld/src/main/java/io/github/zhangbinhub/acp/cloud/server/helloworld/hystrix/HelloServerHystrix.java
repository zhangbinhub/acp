package io.github.zhangbinhub.acp.cloud.server.helloworld.hystrix;

import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import io.github.zhangbinhub.acp.cloud.server.helloworld.feign.HelloServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 2018-3-7 23:47
 * @since JDK 11
 */
@Component
public class HelloServerHystrix implements FallbackFactory<HelloServer> {

    private final LogAdapter log;

    @Autowired
    public HelloServerHystrix(LogAdapter log) {
        this.log = log;
    }

    @Override
    public HelloServer create(Throwable cause) {
        log.error("HelloServer hystrix caused by: " + cause.getMessage(), cause);
        return name -> "Hello service Hystrix[" + name + "]";
    }

}
