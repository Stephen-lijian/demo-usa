package com.example.common.file.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public abstract class S3FileException extends Exception {

	private static final long serialVersionUID = 5007174784360677749L;
	
	protected String code;

}
