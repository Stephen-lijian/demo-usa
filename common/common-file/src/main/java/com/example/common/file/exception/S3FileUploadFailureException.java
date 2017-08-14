package com.example.common.file.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class S3FileUploadFailureException extends S3FileException {/**
	 * 
	 */
	private static final long serialVersionUID = -2165781299824711014L;
	
	public S3FileUploadFailureException() {
		this.code = S3FileExceptionResponse.uploadFailure.getCode();
	}

}
