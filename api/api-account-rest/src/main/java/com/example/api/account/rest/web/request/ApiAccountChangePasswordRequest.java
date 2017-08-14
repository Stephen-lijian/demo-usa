package com.example.api.account.rest.web.request;

import lombok.Data;

@Data
public class ApiAccountChangePasswordRequest {
	
	private String loginToken;
	
	private Long userId;

	private String oldPassword;
	
	private String newPassword;
	
	private String rePassword;
	
}
