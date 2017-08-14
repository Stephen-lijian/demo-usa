package com.example.api.passport.rest.web.plaindata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiPassportLoginRequest {
	private String name;
	
	private String password;
	
	private String captcha;
}
