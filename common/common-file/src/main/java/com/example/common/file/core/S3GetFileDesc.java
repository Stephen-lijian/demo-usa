package com.example.common.file.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3GetFileDesc {
	
	private String bucketName;
	
	private String key;
}
