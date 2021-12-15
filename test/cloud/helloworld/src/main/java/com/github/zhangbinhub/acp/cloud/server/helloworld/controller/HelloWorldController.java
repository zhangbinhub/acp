package com.github.zhangbinhub.acp.cloud.server.helloworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.github.zhangbinhub.acp.core.client.exceptions.HttpException;
import com.github.zhangbinhub.acp.boot.exceptions.ServerException;
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import com.github.zhangbinhub.acp.boot.tools.IpTools;
import com.github.zhangbinhub.acp.cloud.server.helloworld.feign.HelloServer;
import com.github.zhangbinhub.acp.cloud.server.helloworld.feign.WorldServer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangbin by 2018-3-6 15:34
 * @since JDK 11
 */
@RestController
@RefreshScope
public class HelloWorldController {

    private final LogAdapter logAdapter;

    private final HelloServer helloServer;

    private final WorldServer worldServer;

    private final RestTemplate restTemplate;

    @Value("${acp.test.properties}")
    private String properties;

    @Autowired
    public HelloWorldController(HelloServer helloServer, WorldServer worldServer, LogAdapter logAdapter, @Qualifier(value = "customerRestTemplateTest") RestTemplate restTemplate) {
        this.helloServer = helloServer;
        this.worldServer = worldServer;
        this.logAdapter = logAdapter;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> helloWorld(@RequestBody String content) throws ServerException {
//        throw new ServerException("测试异常");
//        String respon = helloServer.fromClient(content) + ";" + worldServer.fromClient(content) + "; properties=" + properties;
        String respon = helloServer.fromClient(content) + ";" + worldServer.fromClientTest() + "; properties=" + properties;
        logAdapter.info(respon);
        return ResponseEntity.ok(respon);
    }

    @GetMapping(value = "/helloworld", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> helloWorldGet(HttpServletRequest request, @RequestParam String name) throws HttpException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        requestHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);
        requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://atomic-world/world?name={1}", HttpMethod.GET, requestEntity, String.class, name);
        System.out.println(responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody());
        return responseEntity;
    }

    @GetMapping(value = "/open/ips", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> ips(HttpServletRequest request) {
        String ip = IpTools.getRemoteIP(request);
        return ResponseEntity.ok(ip);
    }

}
