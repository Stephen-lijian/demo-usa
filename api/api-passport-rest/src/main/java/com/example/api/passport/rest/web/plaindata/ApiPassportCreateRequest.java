package com.example.api.passport.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiPassportCreateRequest {
	
	/** 默认为个人用户，不是机构用户 */
	private int accountType = 0;

	private String name;
	
	
}
