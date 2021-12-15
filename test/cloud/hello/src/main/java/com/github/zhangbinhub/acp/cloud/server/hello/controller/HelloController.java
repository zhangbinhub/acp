package com.github.zhangbinhub.acp.cloud.server.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.zhangbinhub.acp.boot.interfaces.LogAdapter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangbin by 2018-3-5 14:00
 * @since JDK 11
 */
@RestController
public class HelloController {

    private final LogAdapter logAdapter;

    @Autowired
    public HelloController(LogAdapter logAdapter) {
        this.logAdapter = logAdapter;
    }

    @GetMapping(value = "/hellor", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> hello(@RequestParam String name) {
        String respon = "hello response: name=" + name;
        logAdapter.info(respon);
        return ResponseEntity.ok(respon);
    }

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> hello(HttpServletRequest request, @RequestParam String name) {
        String respon = "hello response: name=" + name;
        logAdapter.info(respon);
        return ResponseEntity.ok(respon);
    }

}
