package com.example.api.account.rest.web.response;

import lombok.Data;

@Data
public class ApiAccountLoginCaptchaResp {
	
	private String message;
	
	private String captcha;

}
