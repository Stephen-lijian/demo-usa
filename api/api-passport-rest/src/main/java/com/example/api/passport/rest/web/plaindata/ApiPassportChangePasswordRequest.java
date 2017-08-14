package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportChangePasswordRequest {
	
	private String loginToken;
	
	private Long userId;

	private String oldPassword;
	
	private String newPassword;
	
	private String rePassword;
	
}
