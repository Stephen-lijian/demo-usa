package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportRegistResponse {
	
	private String message;
	
	private boolean canTry = true;
	
	private String captcha = "";
	
	private int tryTimes = -1;
	
	private int maxTimes = -1;
	
}
