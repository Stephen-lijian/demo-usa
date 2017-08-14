package com.example.common.plaindata.base;

import lombok.Getter;

public enum AppBaseResponse {
	
	SUCCESS("app.resp.success"),
	TIMEOUT("app.resp.timeout"),
	FAIL("app.resp.fail");
	
	@Getter
	private String code;
	
	AppBaseResponse(String code) {
		this.code = code;
	}
	
}
