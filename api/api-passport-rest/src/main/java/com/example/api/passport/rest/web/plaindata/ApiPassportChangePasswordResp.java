package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportChangePasswordResp {
	
	private String message;
	
	private boolean result = true;
}
