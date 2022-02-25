package pers.acp.test.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import pers.acp.test.application.entity.primary.TableOne;
import pers.acp.test.application.po.TestJsonPo;
import pers.acp.test.application.po.TestPo;
import pers.acp.test.application.repo.primary.TableRepo;
import io.github.zhangbinhub.acp.core.CommonTools;
import pers.acp.test.application.vo.TestJsonVo;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by zhangbin on 2017/4/26.
 */
@Validated
@RestController
@RequestMapping("/boot")
@Api(tags = {"测试接口"})
public class TestController {

    private final LogAdapter log;

    private final TableRepo tableRepo;

    private final ObjectMapper mapper;

    @Value("${logging.file.path}")
    private String s1;

    @Value("${spring.thymeleaf.cache}")
    private String s2;

    @Value("${info.version}")
    private String s3;

    @Autowired
    public TestController(LogAdapter log, TableRepo tableRepo, ObjectMapper mapper) {
        this.log = log;
        this.tableRepo = tableRepo;
        this.mapper = mapper;
    }

    @ApiOperation(value = "测试 hello", notes = "返回项目绝对路径")
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResponseEntity<Object> home() throws UnknownHostException {
        return ResponseEntity.ok("home，" + CommonTools.getWebRootAbsPath() + "，" + InetAddress.getLocalHost().getHostAddress());
    }

    @ApiOperation(value = "测试 rest 接口1", notes = "返回数据库中记录")
    @RequestMapping(value = "/rest1/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TableOne>> post1(@PathVariable String name,
                                                @ApiParam(value = "pwd", required = true) @NotEmpty(message = "pwd不能为空") @RequestParam String pwd) {
        log.info("name1:" + name + ",pwd1:" + pwd);
        log.debug("name1:" + name + ",pwd1:" + pwd);
        log.info(CommonTools.getWebRootAbsPath());
        List<TableOne> tableOneList = tableRepo.findAll();
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        return ResponseEntity.ok(tableOneList);
    }

    @ApiIgnore
    @RequestMapping(value = "/rest2/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> post2(@PathVariable String name, @RequestParam String pwd) {
        log.info("name2:" + name + ",pwd2:" + pwd);
        log.info(CommonTools.getWebRootAbsPath());
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", name + pwd);
        return ResponseEntity.ok(objectNode.toString());
    }

    @ApiOperation(value = "测试 rest 接口2", notes = "返回字符串")
    @RequestMapping(value = "/rest2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> post2(@ApiParam(value = "str", required = true) @NotEmpty(message = "str不能为空") @RequestBody String str) {
        log.info("str:" + str);
        log.info(CommonTools.getWebRootAbsPath());
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", str);
        return ResponseEntity.ok(objectNode.toString());
    }

    @ApiOperation(value = "测试 rest 接口3", notes = "返回字符串")
    @RequestMapping(value = "/rest3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> post3(@RequestBody @Valid TestPo testPo) {
        log.info("s1:" + testPo.getS1());
        log.info("i1:" + testPo.getI1());
        log.info(CommonTools.getWebRootAbsPath());
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("result2", testPo.toString());
        return ResponseEntity.ok(objectNode.toString());
    }

    @ApiOperation(value = "测试 rest 接口", notes = "参数为Map<String, Object>")
    @RequestMapping(value = "/map", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postMap(@RequestBody Map<String, Object> body) throws IOException {
        log.info("params:");
        body.forEach((key, value) -> log.info("key=" + key + " value=" + value));
        return ResponseEntity.ok(mapper.writeValueAsString(body));
    }

    @ApiOperation(value = "测试 rest 接口", notes = "参数为Map<String, Object>")
    @RequestMapping(value = "/mapform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> postMapForm(@RequestParam Map<String, Object> body) throws IOException {
        log.info("params:");
        body.forEach((key, value) -> log.info("key=" + key + " value=" + value));
        return ResponseEntity.ok(mapper.writeValueAsString(body));
    }


    @ApiOperation(value = "测试异步 rest 接口", notes = "返回字符串")
    @RequestMapping(value = "/restasync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<Object> postAsync(@ApiParam(value = "str", required = true) @NotEmpty(message = "str不能为空") @RequestBody String str) {
        System.out.println("外部线程：" + Thread.currentThread().getName());
        return () -> {
            System.out.println("内部线程：" + Thread.currentThread().getName());
            log.info("str:" + str);
            Thread.sleep(3000);
            log.info(CommonTools.getWebRootAbsPath());
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("result2", str);
            return ResponseEntity.ok(objectNode.toString());
        };
    }

    @ApiOperation(value = "测试 rest 接口", notes = "参数为json")
    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestJsonVo> testJson(@RequestBody TestJsonPo testJsonPo) {
        TestJsonVo testJsonVo = new TestJsonVo();
        testJsonVo.setNameResult(testJsonPo.getNameValue());
        testJsonVo.setCodeResult(testJsonPo.getCodeValue());
        return ResponseEntity.ok(testJsonVo);
    }
}
