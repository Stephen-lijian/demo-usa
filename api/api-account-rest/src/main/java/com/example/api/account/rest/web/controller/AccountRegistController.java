package com.example.api.account.rest.web.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.account.rest.web.request.ApiAccountRegistRequest;
import com.example.api.account.rest.web.response.ApiAccountRegistResp;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.exception.account.AccountNameIncorrectException;
import com.example.common.exception.account.AccountPasswordIncorrectException;
import com.example.common.file.core.S3GetFileDesc;
import com.example.common.file.core.S3FileHandler;
import com.example.common.file.core.S3PutFileDesc;
import com.example.common.plaindata.account.AccountCreateRequest;
import com.example.common.plaindata.account.MsAccountCreateRequest;
import com.example.common.plaindata.account.MsAccountCreateResponse;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountRegistController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	@Autowired
	private S3FileHandler s3FileHandler;
	
	
	@PutMapping(value="/accounts/regist", produces="application/json")
	@PostMapping(value="/accounts/regist", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAccountRegistResp>> handle(ApiAccountRegistRequest requestParam, @RequestParam("headimg") Part headimg, Locale locale )  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiAccountRegistResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				
				String message = messageSource.getMessage( AppBaseResponse.TIMEOUT.getCode(), new Object[0], locale);
				ApiAccountRegistResp resp = new ApiAccountRegistResp();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData( requestParam, headimg );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiAccountRegistResp resp = new ApiAccountRegistResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiAccountRegistResp resp = new ApiAccountRegistResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}
	
	private void sendData( ApiAccountRegistRequest requestParam, Part headimg ) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		// upload img
		uploadHeadimg( requestParam, headimg );
		
		// post regist
		postRegist( requestParam );
	}
	
	

	private void uploadHeadimg(ApiAccountRegistRequest requestParam,
			Part headimg) throws Exception {
		S3PutFileDesc s3PutFileDesc = new S3PutFileDesc(headimg.getInputStream(),headimg.getSize(),headimg.getSubmittedFileName(),headimg.getContentType());
		S3GetFileDesc s3GetFileDesc = s3FileHandler.putFile( s3PutFileDesc );
		requestParam.setHeadimgS3BucketName( s3GetFileDesc.getBucketName() );
		requestParam.setHeadimgS3Key( s3GetFileDesc.getKey() ); 
	}
	
	private void postRegist(ApiAccountRegistRequest requestParam) throws Exception {
		String url = "";
		
		MsAccountCreateRequest requestBody = new MsAccountCreateRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		AccountCreateRequest request = new AccountCreateRequest(); 
		request.setBody(requestBody);
		
		Class<MsAccountCreateResponse> responseType = MsAccountCreateResponse.class;
		Map<String,String> uriVariables = new HashMap<>();
		
		try {
			ResponseEntity<MsAccountCreateResponse> entity = restTemplate.postForEntity( url, request, responseType, uriVariables );
			
			// success
			if ( 200 == entity.getStatusCodeValue() && 0==entity.getBody().getCode() ) {
				return;
			}
			
			// fail
			throw new Exception( entity.getBody().getMessage() );
		} catch (Exception e) {
			e.printStackTrace();
			
			// error
			throw new Exception( AppBaseResponse.FAIL.getCode() );
		}
		
	}

	private void checkValid( ApiAccountRegistRequest requestParam ) throws Exception {
		String name = requestParam.getName();
		if ( StringUtils.isEmpty(name) || name.length()<6 ) {
			throw new AccountNameIncorrectException();
		}
		
		String password = requestParam.getPassword();
		if ( StringUtils.isEmpty(password) || password.length()<6 ) {
			throw new AccountPasswordIncorrectException();
		}

		// repassword
		
		// times limited or not 
	}

}
