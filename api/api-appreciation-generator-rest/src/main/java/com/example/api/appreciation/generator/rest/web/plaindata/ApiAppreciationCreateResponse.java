package com.example.api.appreciation.generator.rest.web.plaindata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAppreciationCreateResponse {
	
	private Long id;
	
	private String message = "success";
	
	public ApiAppreciationCreateResponse(Long id) {
		this.id = id;
	}
	
}
