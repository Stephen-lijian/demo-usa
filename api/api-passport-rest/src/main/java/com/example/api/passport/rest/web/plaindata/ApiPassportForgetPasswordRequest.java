package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportForgetPasswordRequest {
	
	private String step;
	
	// email or mobile
	private String address;
	
	private String captcha;
	
	
}
