package com.traveljournal.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthConfig {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String tokenUri;
	private String userInfoUri;
}