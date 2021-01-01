package pers.acp.spring.cloud.server.helloworld.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pers.acp.spring.cloud.server.helloworld.hystrix.WorldServerHystrix;

/**
 * @author zhangbin by 2018-3-6 15:32
 * @since JDK 11
 */
@FeignClient(value = "atomic-world", fallbackFactory = WorldServerHystrix.class)
public interface WorldServer {

    @RequestMapping(value = "/world", method = RequestMethod.GET)
    String fromClient(@RequestParam(value = "name") String name);

    @RequestMapping(value = "/world-test", method = RequestMethod.GET)
    String fromClientTest();

}
