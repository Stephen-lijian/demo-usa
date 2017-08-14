package com.example.common.file.core;

import java.io.InputStream;

import com.example.common.file.exception.S3FileException;


public interface S3FileHandler {
	

	S3GetFileDesc putFile( S3PutFileDesc s3PutFile ) throws S3FileException;
	
	S3GetFileDesc[] putFiles( S3PutFileDesc[] s3PutFiles ) throws S3FileException;

	InputStream getInputStream(String keyName) throws S3FileException;
	
	void removeFile( String bucketName, String key ) throws S3FileException;
	
	void removeFiles( S3DeleteFileDesc... s3DeleteFileDescs ) throws S3FileException;
}
