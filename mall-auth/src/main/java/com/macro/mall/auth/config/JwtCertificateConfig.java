package com.macro.mall.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @title: JwtCertificateConfig
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/12 0012 7:42
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtCertificateConfig {
    /**
     * 证书名称
     */
    private String keyPairName;


    /**
     * 证书别名
     */
    private String keyPairAlias;

    /**
     * 证书私钥
     */
    private String keyPairSecret;

    /**
     * 证书存储密钥
     */
    private String keyPairStoreSecret;
}
