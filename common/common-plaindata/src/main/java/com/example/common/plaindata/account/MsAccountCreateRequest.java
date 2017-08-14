package com.example.common.plaindata.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsAccountCreateRequest {
	
	private Long id;

	private String email;

	private String mobile;
	
	private String password;

	private String headimgUrl;

}
