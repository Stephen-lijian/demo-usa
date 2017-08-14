package com.example.api.file.rest.web.controller;

import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.file.rest.web.plaindata.ApiFileUploadResp;
import com.example.common.file.core.S3FileHandler;
import com.example.common.file.core.S3GetFileDesc;
import com.example.common.file.core.S3PutFileDesc;

@RestController
public class FileUploadRestController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	@Qualifier("taskExecutor")
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;

	@Autowired
	private S3FileHandler s3FileHandler;
	

	@PutMapping(value="/files", produces="application/json")
	@PostMapping(value="/files/upload", produces="application/json")
	public DeferredResult<ResponseEntity<ApiFileUploadResp>> handle(
			@RequestParam("file") Part file )  {
		long timeout = 60000L;
		DeferredResult<ResponseEntity<ApiFileUploadResp>> deferredResult = new DeferredResult<>( timeout );
	
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
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ApiFileUploadResp()) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					String imgkey = sendData( file );
					deferredResult.setResult( 
						ResponseEntity.ok().body( new ApiFileUploadResp(imgkey) ) 
					);
				} catch (Exception e) {
					deferredResult.setResult( 
						ResponseEntity.ok().body(new ApiFileUploadResp()) 
					);
				}
			}
		});
		
		return deferredResult;
	}
	 
	
	private String sendData(Part part ) throws Exception {
		return uploadImgs( part );
	}


	private String uploadImgs( Part part ) throws Exception {
		S3PutFileDesc s3PutFileDesc = new S3PutFileDesc(part.getInputStream(),part.getSize(),part.getSubmittedFileName(),part.getContentType());
		S3GetFileDesc s3GetFile = s3FileHandler.putFile( s3PutFileDesc );
		return s3GetFile.getBucketName() + "::" + s3GetFile.getKey();
	}

}
