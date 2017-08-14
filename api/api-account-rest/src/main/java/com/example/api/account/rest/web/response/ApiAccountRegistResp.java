package com.example.api.account.rest.web.response;

import lombok.Data;

@Data
public class ApiAccountRegistResp {
	
	private String message;
	
	private boolean canTry = true;
	
	private String captcha = "";
	
	private int tryTimes = -1;
	
	private int maxTimes = -1;
	
}
