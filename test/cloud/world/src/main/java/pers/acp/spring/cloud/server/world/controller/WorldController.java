package pers.acp.spring.cloud.server.world.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangbin by 2018-3-5 14:05
 * @since JDK 11
 */
@RestController
public class WorldController {

    @GetMapping(value = "/world", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> world(@RequestParam String name) {
        return ResponseEntity.ok("world response: name=" + name);
    }

}
