package com.example.common.file.core;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3PutFileDesc {
	
	private InputStream inputStream;
	
	private long length;
	
	private String filename;
	
	private String contentType;

}
