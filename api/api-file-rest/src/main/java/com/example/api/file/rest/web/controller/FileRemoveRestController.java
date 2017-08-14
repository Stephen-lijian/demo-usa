package com.example.api.file.rest.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.file.core.S3DeleteFileDesc;
import com.example.common.file.core.S3FileHandler;

@RestController
public class FileRemoveRestController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	@Qualifier("taskExecutor")
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;

	@Autowired
	private S3FileHandler s3FileHandler;
	

	@DeleteMapping(value="/files", produces="application/json")
	@PostMapping(value="/files/delete", produces="application/json")
	public DeferredResult<ResponseEntity<Void>> handle(
			@RequestParam("files[]") String[] files )  {
		long timeout = 60000L;
		DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>( timeout );
	
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), deferredResult.getResult() );
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build() );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData( files );
					deferredResult.setResult( 
						ResponseEntity.ok().build() 
					);
				} catch (Exception e) {
					deferredResult.setResult( 
						ResponseEntity.ok().build() 
					);
				}
			}
		});
		
		return deferredResult;
	}
	 
	
	private void sendData( String[] files ) throws Exception {
		int len = files.length;
		S3DeleteFileDesc[] s3DeleteFileDescs = new S3DeleteFileDesc[len];
		for ( int i=0; i<len; i++ ) {
			String file = files[i];
			String[] arr = StringUtils.splitByWholeSeparator(file, "::");
			String bucketName = arr[0];
			String key = arr[1];
			s3DeleteFileDescs[i] = new S3DeleteFileDesc( bucketName, key );
		}
		s3FileHandler.removeFiles(s3DeleteFileDescs );
	}

}
