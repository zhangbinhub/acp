import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import io.github.zhangbinhub.acp.core.client.exceptions.HttpException;
import io.github.zhangbinhub.acp.core.client.http.HttpCallBack;
import io.github.zhangbinhub.acp.core.client.http.HttpClientBuilder;
import io.github.zhangbinhub.acp.core.client.http.RequestParamBuilder;
import io.github.zhangbinhub.acp.core.client.http.ResponseResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 03/07/2019
 * @since JDK 11
 */
class TestHttp {

    @Test
    void doTest() {
        try {
            doPost();
            doPostAsync();
            doPostString();
            doPostBytes();
            doGet();
            doGetHttps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void doPost() throws HttpException {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        map.put("client_id", "test");
        map.put("client_secret", "test");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPost(new RequestParamBuilder("http://127.0.0.1:9090/boot/mapform")
                        .params(map).build());
        System.out.println("doPost -----> " + responseResult);
        System.out.println("doPost -----> " + responseResult.getStatus());
        System.out.println("doPost -----> " + responseResult.getBody());
        System.out.println("doPost -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doPostAsync() throws HttpException, InterruptedException {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        map.put("client_id", "test");
        map.put("client_secret", "test");
        long begin = System.currentTimeMillis();
        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> begin");
        new HttpClientBuilder().build()
                .doPostAsync(new RequestParamBuilder("http://127.0.0.1:9090/boot/mapform")
                        .params(map).build(), new HttpCallBack() {
                    @Override
                    public void onRequestFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> failure");
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + call.request().url());
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + (System.currentTimeMillis() - begin));
                        e.printStackTrace();
                    }

                    @Override
                    public void onRequestResponse(@NotNull Call call, @NotNull ResponseResult responseResult) {
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + responseResult);
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + responseResult.getStatus());
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + responseResult.getBody());
                        System.out.println("doPostAsync [" + Thread.currentThread().getId() + "] -----> " + (System.currentTimeMillis() - begin));
                    }
                });
        Thread.sleep(5000);
    }

    @Test
    void doPostString() throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("param1", "尼玛");
        body.put("param2", "3");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPostJson(new RequestParamBuilder("http://127.0.0.1:9090/boot/map")
                        .body(body.toString()).build());
        System.out.println("doPostString -----> " + responseResult);
        System.out.println("doPostString -----> " + responseResult.getStatus());
        System.out.println("doPostString -----> " + responseResult.getBody());
        System.out.println("doPostString -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doPostBytes() throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("param1", "尼玛");
        body.put("param2", "3");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doPostBytes(new RequestParamBuilder("http://127.0.0.1:9090/boot/map")
                        .body(body.toString().getBytes()).build());
        System.out.println("doPostBytes -----> " + responseResult);
        System.out.println("doPostBytes -----> " + responseResult.getStatus());
        System.out.println("doPostBytes -----> " + responseResult.getBody());
        System.out.println("doPostBytes -----> " + (System.currentTimeMillis() - begin));
    }

    @Test
    void doGet() throws HttpException {
        Map<String, String> map = new HashMap<>();
        map.put("pwd", "password");
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder().build()
                .doGet(new RequestParamBuilder("http://127.0.0.1:9090/boot/rest1/testget")
                        .params(map).build());
        System.out.println("doGet -----> " + responseResult);
        System.out.println("doGet -----> " + responseResult.getStatus());
        System.out.println("doGet -----> " + responseResult.getBody());
        System.out.println("doGet -----> " + (System.currentTimeMillis() - begin));
    }

    /**
     * 开启https请求支持，可同时发起http和https请求
     */
    @Test
    void doGetHttps() throws HttpException {
        long begin = System.currentTimeMillis();
        ResponseResult responseResult = new HttpClientBuilder()
                .disableSslValidation(true)
//                .sslProtocolVersion("TLSv1.2")
                .build()
                .doGet(new RequestParamBuilder("https://github.com/zhangbin1010/acp-admin").build());
        System.out.println("doGetHttps -----> " + responseResult);
        System.out.println("doGetHttps -----> " + responseResult.getStatus());
        System.out.println("doGetHttps -----> " + responseResult.getBody());
        System.out.println("doGetHttps -----> " + (System.currentTimeMillis() - begin));
    }

}
