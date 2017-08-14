package com.example.api.account.rest.web.request;

import lombok.Data;

@Data
public class ApiAccountRegistCaptchaRequest {
	
	/** 0-mobile, 1-email */
	private int type;
	
	private String address;
	
}
