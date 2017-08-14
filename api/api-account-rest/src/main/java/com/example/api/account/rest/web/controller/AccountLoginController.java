package com.example.api.account.rest.web.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.account.rest.web.request.ApiAccountLoginRequest;
import com.example.api.account.rest.web.response.ApiAccountLoginResp;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.plaindata.account.MsAccountLoginRequest;
import com.example.common.plaindata.account.MsAccountLoginRequest;
import com.example.common.plaindata.account.MsAccountLoginResponse;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountLoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	@PostMapping(value="/accounts/login", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAccountLoginResp>> handle( ApiAccountLoginRequest requestParam, Locale locale ) {
		
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiAccountLoginResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiAccountLoginResp resp = new ApiAccountLoginResp();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					String loginToken = sendData( requestParam );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiAccountLoginResp resp = new ApiAccountLoginResp();
					resp.setMessage( message ); 
					resp.setLoginToken( loginToken );
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiAccountLoginResp resp = new ApiAccountLoginResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}
 
	private String sendData( ApiAccountLoginRequest requestParam) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		return postLogin( requestParam );
	}

	private String postLogin(ApiAccountLoginRequest requestParam) throws Exception {
		// FIXME
		String url = "";
		
		MsAccountLoginRequest requestBody = new MsAccountLoginRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		MsAccountLoginRequest request = new MsAccountLoginRequest(); 
		request.setBody(requestBody);
		
		Class<MsAccountLoginResponse> responseType = MsAccountLoginResponse.class;
		Map<String,String> uriVariables = new HashMap<>();
		
		try {
			ResponseEntity<MsAccountLoginResponse> entity = restTemplate.postForEntity( url, request, responseType, uriVariables );
			
			// success
			if ( 200 == entity.getStatusCodeValue() && 0==entity.getBody().getCode() ) {
				return entity.getBody().getResult();
			}
			
			// fail
			throw new Exception( entity.getBody().getMessage() );
		} catch (RestClientException e) {
			e.printStackTrace();
			
			// error
			throw new Exception( AppBaseResponse.FAIL.getCode() );
		}
	}

	private void checkValid(ApiAccountLoginRequest requestParam) throws Exception {
		
		// name
		
		// password
		
		// captcha in cache
		
	}
	
	
//	private void a() {	
//		ApiAccountLoginRequest requestParam = new ApiAccountLoginRequest(name,password,captcha); 
//		try {
//			checkValid( requestParam );
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//		
//		String url = "";
//		
//		AccountLoginRequestBody requestBody = new AccountLoginRequestBody();
//		BeanUtils.copyProperties( requestParam, requestBody );
//		AccountLoginRequest request = new AccountLoginRequest(); 
//		request.setBody(requestBody);
//		
//		Class<AccountLoginResp> responseType = AccountLoginResp.class;
//		Map<String,String> uriVariables = new HashMap<>();
//		
//		try {
//			ResponseEntity<AccountLoginResp> entity = restTemplate.postForEntity( url, request, responseType, uriVariables );
//			
//			// success
//			if ( 200 == entity.getStatusCodeValue() && 0==entity.getBody().getCode() ) {
//				return ResponseEntity.ok().build();
//			}
//			
//			// fail
//			return ResponseEntity.badRequest().body( entity.getBody().getMessage() );
//		} catch (RestClientException e) {
//			e.printStackTrace();
//			
//			// error
//			return ResponseEntity.badRequest().body( "error" );
//		}
//	}

}
