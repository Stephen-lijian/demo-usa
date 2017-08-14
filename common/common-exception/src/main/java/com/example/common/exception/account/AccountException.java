package com.example.common.exception.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.example.common.exception.MsException;


@Data
@EqualsAndHashCode(callSuper=false)
public abstract class AccountException extends MsException {
	
	private static final long serialVersionUID = -198749453605945918L;
	
	public AccountException() {
		this.code = AccountExceptionResponse.error.getCode();
	}
}
