package com.example.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public abstract class MsException extends Exception {

	private static final long serialVersionUID = -8391393062479120200L;
	
	protected String code;

}
