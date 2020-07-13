package pers.acp.spring.cloud.server.helloworld.hystrix;

import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.interfaces.LogAdapter;
import pers.acp.spring.cloud.server.helloworld.feign.HelloServer;

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
