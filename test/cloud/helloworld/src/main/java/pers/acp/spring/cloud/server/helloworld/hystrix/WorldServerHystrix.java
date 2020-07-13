package pers.acp.spring.cloud.server.helloworld.hystrix;

import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.interfaces.LogAdapter;
import pers.acp.spring.cloud.server.helloworld.feign.WorldServer;

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
        return name -> "World service Hystrix[" + name + "]";
    }

}
