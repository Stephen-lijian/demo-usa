package com.example.common.exception.account;

import lombok.Getter;

public enum AccountExceptionResponse {
	
	error("app.resp.account.error"),
	
	accountNameIncorrect("app.resp.account.accountNameIncorrect"),

	accountNameExisted("app.resp.account.accountNameExisted"),
	
	accountNameNotFound("app.resp.account.accountNameNotFound"),

	passwordIncorrect("app.resp.account.passwordIncorrect"),
	
	passwordNotEqual("app.resp.account.passwordNotEqual"),

	passwordLenIncorrect("app.resp.account.passwordLenIncorrect"),
	
	mobileIncorrect("app.resp.account.mobileIncorrect"),

	mobileRegisted("app.resp.account.mobileRegisted"),

	emailIncorrect("app.resp.account.emailIncorrect"),

	emailRegisted("app.resp.account.emailRegisted");
	
	@Getter 
	private String code; 
	
	AccountExceptionResponse(String code){
		this.code = code;
	};
	
}
