package com.example.common.file.exception;

import lombok.Getter;

public enum S3FileExceptionResponse {
	
	uploadFailure("app.resp.file.s3.uploadFailure");
	
	@Getter 
	private String code; 
	
	S3FileExceptionResponse(String code){
		this.code = code;
	};
	
}
