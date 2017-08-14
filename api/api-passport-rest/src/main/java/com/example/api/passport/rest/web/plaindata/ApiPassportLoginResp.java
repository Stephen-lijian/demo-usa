package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportLoginResp {
	
	private String message = "success";
	
	private String accessToken;

	private int tokenType;
	
	private String refreshToken;

	private long expiredIn;
}
