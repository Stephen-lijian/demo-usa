package com.example.common.message.account;

import lombok.Data;

@Data
public class AccountCreatedMessage {

	private Long id;
	
	private String email;
	
	private String mobile;
	
	private String firstName;
	
	private String lastName;
	
	private String headimg;
	
}
