package com.example.common.exception.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountPasswordNotEqualException extends AccountException {
	
	private static final long serialVersionUID = -9145155602802176884L;
	
	public AccountPasswordNotEqualException() {
		this.code = AccountExceptionResponse.passwordNotEqual.getCode();
	}


}
