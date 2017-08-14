package com.example.api.account.rest.web.request;

import lombok.Data;

@Data
public class ApiAccountForgetPasswordRequest {
	
	private String step;
	
	// email or mobile
	private String address;
	
	private String captcha;
	
	
}
