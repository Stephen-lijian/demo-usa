package com.example.api.sample.rest.web.request;

import lombok.Data;

@Data
public class MissingFieldsRequestParam {

	private Long id;
	
	private String name;
	
	private String missing = "OK";
	
}
