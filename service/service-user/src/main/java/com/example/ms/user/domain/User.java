package com.example.ms.user.domain;

import lombok.Data;

@Data
public class User {
	
	private Long id;
	
	private String email = "";
	
	private String mobile = "";
	
	private String[] appTokens = new String[0];
	
	private String firstName;
	
	private String lastName;
	
	
	
//	private Long createAt;
//	
//	private Long updateAt;
	
//	private String createBy;
//	
//	private Strign updateBy;

}
