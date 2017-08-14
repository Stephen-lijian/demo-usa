package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportLoginCaptchaResp {
	
	private String message;
	
	private String captcha;

}
