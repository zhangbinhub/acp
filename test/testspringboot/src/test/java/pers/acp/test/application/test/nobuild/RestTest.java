package pers.acp.test.application.test.nobuild;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pers.acp.test.application.BaseTest;

/**
 * @author zhangbin by 28/04/2018 15:54
 * @since JDK 11
 */
class RestTest extends BaseTest {

    @Test
    void testGet() {
        String name = "abc";
        String pwd = "qwertyyu";
        ResponseEntity<JsonNode> result = testRestTemplate.getForEntity("/boot/rest2/" + name + "?pwd={1}", JsonNode.class, pwd);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(result.getBody().get("result2").textValue(), name + pwd);
    }

    @Test
    void testPost() {
        String request = "sdfasdafsfa范德萨范德萨";
        ResponseEntity<JsonNode> result = testRestTemplate.postForEntity("/boot/rest2", request, JsonNode.class);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(result.getBody().get("result2").textValue(), request);
    }

    @Test
    void testFormPost() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "你好");
        params.add("number", "23");
        params.add("money", "23.98");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<JsonNode> result = testRestTemplate.postForEntity("/boot/mapform", requestEntity, JsonNode.class);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        System.out.println(result.getBody().toString());
    }

}
