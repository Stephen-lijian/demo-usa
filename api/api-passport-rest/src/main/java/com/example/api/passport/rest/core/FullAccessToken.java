package com.example.api.passport.rest.core;

import lombok.Data;

@Data
public class FullAccessToken {
	
	private String accessToken;
	
	private long userId;
	
	private String sessionId;
	
	private Platform platform;
	
	private TokenType tokenType = TokenType.DEFAULT;
	
	public String getKey() {
		return accessToken;
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
