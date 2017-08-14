package com.example.api.passport.rest.core;

import lombok.Data;

@Data
public class FullUserPlatform {
	
	private long userId;
	
	private Platform platform;
	
	private String accessToken;
	
	private String refreshToken;
	
	private TokenType tokenType = TokenType.DEFAULT;
	
	private long expireIn;
	
	public String getKey() {
		return new StringBuilder()
			.append(userId).append("-")
			.append(platform.getValue())
			.toString();
	}
	
	public String getValue() {
		return new StringBuilder()
			.append(accessToken).append("-")
			.append(refreshToken).append("-")
			.append(tokenType.getValue()).append("-")
			.append(expireIn)
			.toString();
	}
	
}
