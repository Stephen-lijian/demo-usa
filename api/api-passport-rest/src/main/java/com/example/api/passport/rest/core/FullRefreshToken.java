package com.example.api.passport.rest.core;

import lombok.Data;

@Data
public class FullRefreshToken {

	private String refreshToken;
	
	private long userId;
	
	private String sessionId;
	
	private Platform platform;
	
	private TokenType tokenType = TokenType.DEFAULT;

	public String getKey() {
		return refreshToken;
	}
	
	public String getValue() {
		return new StringBuilder()
			.append(userId).append("-")
			.append(sessionId).append("-")
			.append(platform.getValue()).append("-")
			.append(tokenType.getValue())
			.toString();
	}
}
