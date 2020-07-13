package pers.acp.spring.cloud.server.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 2018-3-5 13:56
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

    @Bean("customerRestTemplateTest")
    @LoadBalanced
    public RestTemplate restTemplate(FeignHttpClientProperties feignHttpClientProperties) throws HttpException {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory(
                new HttpClientBuilder().maxTotalConn(feignHttpClientProperties.getMaxConnections())
                        .connectTimeOut(feignHttpClientProperties.getConnectionTimeout())
                        .timeToLive(feignHttpClientProperties.getTimeToLive())
                        .timeToLiveTimeUnit(feignHttpClientProperties.getTimeToLiveUnit())
                        .followRedirects(feignHttpClientProperties.isFollowRedirects())
                        .disableSslValidation(feignHttpClientProperties.isDisableSslValidation())
                        .build().getBuilder().build()));
    }

}
