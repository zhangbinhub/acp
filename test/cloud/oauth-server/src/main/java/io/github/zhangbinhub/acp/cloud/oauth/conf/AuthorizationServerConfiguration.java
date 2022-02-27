package io.github.zhangbinhub.acp.cloud.oauth.conf;

import io.github.zhangbinhub.acp.cloud.oauth.domain.SecurityClientDetailsService;
import io.github.zhangbinhub.acp.cloud.oauth.domain.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author zhangbin by 11/04/2018 14:34
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final RedisConnectionFactory connectionFactory;

    private final AuthenticationManager authenticationManager;

    private final SecurityUserDetailsService securityUserDetailsService;

    private final SecurityClientDetailsService securityClientDetailsService;

    @Autowired
    public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, SecurityUserDetailsService securityUserDetailsService, SecurityClientDetailsService securityClientDetailsService
            , RedisConnectionFactory connectionFactory) {
        this.authenticationManager = authenticationManager;
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityClientDetailsService = securityClientDetailsService;
        this.connectionFactory = connectionFactory;
    }

    private DefaultTokenServices securityTokenService() {
        // 使用默认的 token service，需自定义时，继承 DefaultTokenServices ，重写对应方法即可
        return new DefaultTokenServices();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        DefaultTokenServices securityTokenService = securityTokenService();
//        TokenStore tokenStore = new InMemoryTokenStore(); // token 默认持久化到内存
        // 持久化到 redis
        TokenStore tokenStore = new RedisTokenStore(connectionFactory);
        securityTokenService.setTokenStore(tokenStore);
        securityTokenService.setClientDetailsService(securityClientDetailsService);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsService)
                .tokenServices(securityTokenService)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(securityClientDetailsService);
    }

}
