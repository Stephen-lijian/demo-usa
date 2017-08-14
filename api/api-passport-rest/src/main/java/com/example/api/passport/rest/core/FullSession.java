package com.example.api.passport.rest.core;

import lombok.Data;

@Data
public class FullSession {

	private String sessionId;
	
	private long userId;
	
	private String username;
	
	public String getKey() {
		return sessionId;
	}
	
	public String getValue() {
		return new StringBuilder()
		.append(userId).append("-")
		.append(username)
		.toString();
	}

}
