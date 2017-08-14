package com.example.common.file.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.example.common.file.exception.S3FileException;
import com.example.common.file.exception.S3FileUploadFailureException;

//@Component
//@ConfigurationProperties(prefix="aws.s3")
public final class S3FileHandlerProvider implements S3FileHandler {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public S3FileHandlerProvider( AmazonS3 amazonS3, AsyncTaskExecutor executor ) {
		this.amazonS3 = amazonS3;
		this.executor = executor;
	}
	
	@Setter @Getter
	private String defaultBucketName;
	
	@Setter @Getter
	private String keyPrefix;

	@Setter @Getter
	private long uploadTimeout;
	
//	@Autowired
	private AmazonS3 amazonS3;

//	@Autowired
//	@Qualifier("s3Executor")
	private AsyncTaskExecutor executor;
	
	@AllArgsConstructor	
	class UploadTask implements Callable<PutObjectResult> {
		
		final String bucketName;
		final String keyName;
		final S3PutFileDesc s3PutFileDesc;

		@Override
		public PutObjectResult call() throws Exception {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength( s3PutFileDesc.getLength() );
			metadata.setContentType( s3PutFileDesc.getContentType() );
			PutObjectRequest request = new PutObjectRequest( bucketName, keyName, s3PutFileDesc.getInputStream(), metadata  );
			PutObjectResult result;
			try {
				result = amazonS3.putObject( request );
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				
				if ( e instanceof AmazonServiceException ) {
					AmazonServiceException ase = (AmazonServiceException)e;
					if ( logger.isErrorEnabled() ) {
						logger.error( "[s3-service-error]|{}|{}|{}|{}|{}", 
							new Object[]{
								ase.getRequestId(),
								ase.getErrorType(),
								ase.getErrorCode(),
								ase.getStatusCode(),
								ase.getMessage()
							}
						);
					}
				}
				
				return null;
			}
		}
	}
	
	@AllArgsConstructor	
	class DeleteTask implements Runnable {
		
		final String bucketName;
		final String keyName;

		@Override
		public void run() {
			try {
				amazonS3.deleteObject( bucketName, keyName );
			} catch (Exception e) {
				e.printStackTrace();
				
				if ( e instanceof AmazonServiceException ) {
					AmazonServiceException ase = (AmazonServiceException)e;
					if ( logger.isErrorEnabled() ) {
						logger.error( "[s3-service-error]|{}|{}|{}|{}|{}", 
							new Object[]{
								ase.getRequestId(),
								ase.getErrorType(),
								ase.getErrorCode(),
								ase.getStatusCode(),
								ase.getMessage()
							}
						);
					}
				}
			}
		}
	}
	
	@Override
	public S3GetFileDesc putFile( S3PutFileDesc s3PutFile ) throws S3FileException {
		String bucketName = this.defaultBucketName;
		String key = getKeyName( s3PutFile.getFilename() );
		
		try {
			Future<PutObjectResult> future = executor.submit( new UploadTask(bucketName, key, s3PutFile ) );
			PutObjectResult result = future.get( this.uploadTimeout, TimeUnit.SECONDS );
			if ( result == null ) {
				throw new S3FileUploadFailureException();
			}
			return new S3GetFileDesc( bucketName, key );
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new S3FileUploadFailureException();
		}
	}
	
	@Override
	public S3GetFileDesc[] putFiles( S3PutFileDesc[] s3PutFiles ) throws S3FileException {
		String bucketName = this.defaultBucketName;
		int length = s3PutFiles.length;
		
		S3GetFileDesc[] getFiles = new S3GetFileDesc[length];
		String[] keys = new String[length];
		List<Future<PutObjectResult>> futures = new ArrayList<>(length);
		
		for ( int i=0; i<length; i++ ) {
			S3PutFileDesc s3PutFile = s3PutFiles[i];
			keys[i] = getKeyName( s3PutFile.getFilename() );
			String key = keys[i];
			
			Future<PutObjectResult> future = executor.submit( new UploadTask(bucketName, key, s3PutFile ) );
			futures.add( future );
		}
		
		// FIXME 此处异常需要细致处理，避免部分上传失败
		for ( int i=0; i<length; i++ ) {
			Future<PutObjectResult> future = futures.get(i);
			try {
				PutObjectResult result = future.get( this.uploadTimeout, TimeUnit.SECONDS );
				if ( result == null ) {
					throw new S3FileUploadFailureException();
				}
				S3GetFileDesc getFile = new S3GetFileDesc( bucketName, keys[i] );
				getFiles[i] = getFile;
			} catch (InterruptedException e) {
				e.printStackTrace();
				
				throw new S3FileUploadFailureException();
			} catch (ExecutionException e) {
				e.printStackTrace();
				
				throw new S3FileUploadFailureException();
			} catch (TimeoutException e) {
				e.printStackTrace();
				
				throw new S3FileUploadFailureException();
			}
		}
		return getFiles;
	}
	
	
	
	private String getKeyName(String filename) {
		return new StringBuilder()
			.append( this.keyPrefix ).append( "-") 
			.append( RandomStringUtils.randomAlphabetic(8) ).append( "-") 
			.append( filename )
			.toString();
	}

	
	public InputStream getInputStream(String key) throws S3FileException {
		String bucketName = this.defaultBucketName;
		String keyName = key;
		
		S3Object object = amazonS3.getObject(bucketName, keyName);
		return  object.getObjectContent();
	}

	@Override
	public void removeFile(String bucketName, String key)
			throws S3FileException {
		try {
			executor.submit( new DeleteTask(bucketName, key) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeFiles(S3DeleteFileDesc... s3DeleteFileDescs)
			throws S3FileException {
		
		for ( S3DeleteFileDesc item : s3DeleteFileDescs ) {
			try {
					executor.submit( new DeleteTask(item.getBucketName(), item.getKey()) );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	

}
