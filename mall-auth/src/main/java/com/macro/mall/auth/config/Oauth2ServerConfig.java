package com.macro.mall.auth.config;

import com.macro.mall.auth.component.JwtTokenEnhancer;
import com.macro.mall.auth.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务器配置
 *
 * @author 郭浩伟
 * @date 2022-9-12 08:00:38
 */
@EnableConfigurationProperties(value = JwtCertificateConfig.class)
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenEnhancer jwtTokenEnhancer;

    @Autowired
    private JwtCertificateConfig jwtCertificateConfig;
    @Autowired
    private DataSource dataSource;

    /**
     * 方法实现说明:认证服务器能够给哪些 客户端颁发token  我们需要把客户端的配置 存储到
     * 数据库中 可以基于内存存储和db存储
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    /**
     * 方法实现说明:用于查找我们第三方客户端的组件 主要用于查找 数据库表 oauth_client_details
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 内存配置
     * TODO 可将【reuseRefreshTokens】设为false,将accessToken时间设置短些，如10分钟，
     *      前端每次请求先判断accessToken失效时间，如过期可通过refreshToken接口获取新的token和refreshToken
     *
     * @param clients
     * @throws Exception
     */
    public void configure1(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("admin-app")
                .secret(passwordEncoder.encode("123456"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600 * 24)
                .refreshTokenValiditySeconds(3600 * 24 * 7)
                //.redirectUris("http://localhost:9501/login") //单点登录时配置
                .and()
                .withClient("portal-app")
                .secret(passwordEncoder.encode("123456"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600 * 24)
                .refreshTokenValiditySeconds(3600 * 24 * 7);
    }

    /**
     * 授权服务器的配置
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //配置JWT的内容增强器
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates);
        //使用密码模式需要配置
        endpoints.authenticationManager(authenticationManager)
                //refresh_token是否重复使用 默认true 设置false则使用一次后立即失效
                .reuseRefreshTokens(false)
                //配置加载用户信息的服务
                .userDetailsService(userDetailsService)
                //此处应该是配多余了
                //.accessTokenConverter(jwtAccessTokenConverter())
                //支持GET,POST请求
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                //指定token存储策略是jwt
                .tokenStore(tokenStore())
                .tokenEnhancer(enhancerChain);
    }

    /**
     * 授权服务器安全配置
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许表单认证
        security.allowFormAuthenticationForClients()
                //第三方客户端校验token需要带入 clientId 和clientSecret来校验
                .checkTokenAccess("isAuthenticated()")
                //来获取我们的tokenKey需要带入clientId,clientSecret/获取密钥需要身份认证，使用单点登录时必须配置
                .tokenKeyAccess("isAuthenticated()");
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(jwtCertificateConfig.getKeyPairName()), jwtCertificateConfig.getKeyPairSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCertificateConfig.getKeyPairAlias(), jwtCertificateConfig.getKeyPairStoreSecret().toCharArray());
    }

    /**
     * 方法实现说明:我们颁发的token通过jwt存储
     * jwtTokenStore只需要保存在内存中，因为认证服务器用私钥加密，授权服务器用公钥解密
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
}
