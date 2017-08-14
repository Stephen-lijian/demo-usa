package com.example.api.passport.rest.core;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Data
public class TokenConfig {
	
	private final static int DEFAULT_ACCESS_TOKEN_EXPIRED = 2 * 60;
	
	private final static int DEFAULT_REFRESH_TOKEN_EXPIRED = 30 * 24 * 60;
	
	private final static int DEFAULT_DELAY_EXPIRED = 5;

	/**
	 * accessToken过期时间
	 */
	private int accessTokenExpired = DEFAULT_ACCESS_TOKEN_EXPIRED;
	
	/**
	 * refreshToken过期时间
	 */
	private int refreshTokenExpired = DEFAULT_REFRESH_TOKEN_EXPIRED;
	
	/**
	 * 延迟过期时间
	 */
	private int delayExpired = DEFAULT_DELAY_EXPIRED;
	
}
