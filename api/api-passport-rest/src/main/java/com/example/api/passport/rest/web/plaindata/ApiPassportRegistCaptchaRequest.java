package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportRegistCaptchaRequest {
	
	/** 0-mobile, 1-email */
	private int type;
	
	private String address;
	
}
