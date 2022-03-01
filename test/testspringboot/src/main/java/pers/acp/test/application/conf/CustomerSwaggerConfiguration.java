package pers.acp.test.application.conf;

import io.github.zhangbinhub.acp.boot.base.BaseSwaggerConfiguration;
import io.github.zhangbinhub.acp.boot.conf.SwaggerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author zhang by 27/12/2018
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
public class CustomerSwaggerConfiguration extends BaseSwaggerConfiguration {

    @Autowired
    public CustomerSwaggerConfiguration(@Value("${info.version}") String version,
                                        SwaggerConfiguration swaggerConfiguration) {
        super(version, swaggerConfiguration);
    }

    @Bean
    public Docket createRestApi() {
        return buildDocket("pers.acp.test.application.controller",
                "Test Spring Boot RESTful API",
                "API Document",
                "ZhangBin",
                "https://github.com/zhangbin1010",
                "zhangbin1010@qq.com");
    }
}
