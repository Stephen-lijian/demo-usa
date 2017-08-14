package com.example.common.exception.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountNameNotFoundException extends AccountException {

	private static final long serialVersionUID = 7673342995967893245L;
	
	public AccountNameNotFoundException() {
		this.code = AccountExceptionResponse.accountNameNotFound.getCode();
	}

}
