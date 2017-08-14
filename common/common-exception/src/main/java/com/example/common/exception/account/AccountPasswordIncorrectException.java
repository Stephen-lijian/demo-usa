package com.example.common.exception.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountPasswordIncorrectException extends AccountException {

	private static final long serialVersionUID = -1771334718200903572L;
	
	public AccountPasswordIncorrectException() {
		this.code = AccountExceptionResponse.passwordIncorrect.getCode();
	}


}
