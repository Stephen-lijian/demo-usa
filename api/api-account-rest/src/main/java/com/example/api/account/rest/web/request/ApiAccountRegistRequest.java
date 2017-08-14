package com.example.api.account.rest.web.request;

import lombok.Data;

@Data
public class ApiAccountRegistRequest {
	
	/** 默认为个人用户，不是机构用户 */
	private int accountType = 0;

	private String name;
	
	private String password;
	
	private String repassword;
	
	private String headimgS3BucketName;
	
	private String headimgS3Key;
	
	private String birthday;
	
	private String firstname;
	
	private String lastname;
	
	private int gender;
	
	private String mobile;
	
	private String email;
	
	private String other;
	
}
