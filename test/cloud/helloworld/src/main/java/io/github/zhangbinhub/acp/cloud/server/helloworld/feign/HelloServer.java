package io.github.zhangbinhub.acp.cloud.server.helloworld.feign;

import io.github.zhangbinhub.acp.cloud.server.helloworld.hystrix.HelloServerHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhangbin by 2018-3-6 15:28
 * @since JDK 11
 */
@FeignClient(value = "atomic-hello", fallbackFactory = HelloServerHystrix.class)
public interface HelloServer {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String fromClient(@RequestParam(value = "name") String name);

}
