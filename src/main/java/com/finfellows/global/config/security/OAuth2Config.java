package com.finfellows.global.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class OAuth2Config {
    private final Auth auth = new Auth();

    @Data
    public static class Auth {
        private String tokenSecret;
        private long accessTokenExpirationMsec;
        private long refreshTokenExpirationMsec;
    }

    public Auth getAuth() {
        return auth;
    }
}
