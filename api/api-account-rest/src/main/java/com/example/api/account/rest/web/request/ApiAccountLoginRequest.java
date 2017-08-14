package com.example.api.account.rest.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccountLoginRequest {
	private String name;
	
	private String password;
	
	private String captcha;
}
