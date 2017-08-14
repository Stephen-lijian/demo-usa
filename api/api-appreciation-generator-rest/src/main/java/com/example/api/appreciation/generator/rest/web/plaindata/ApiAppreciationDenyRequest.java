package com.example.api.appreciation.generator.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiAppreciationDenyRequest {
	
	private long apprId;

	private String reason;
}
