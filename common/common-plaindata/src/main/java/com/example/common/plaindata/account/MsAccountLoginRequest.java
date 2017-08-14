package com.example.common.plaindata.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsAccountLoginRequest {
	
	private String name;

	private String password;
	
	private String captcha;

}
