package com.example.api.account.rest.web.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.common.plaindata.account.MsAccountCreateResponse;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountLogoutController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AsyncTaskExecutor executor;
	
	@PostMapping(value="/accounts/logout", produces="application/json")
	public DeferredResult<ResponseEntity<Void>> handle( @RequestHeader("loginToken") String loginToken, Locale locale ) {// @RequestHeader("loginToken") String loginToken )  {
		
		long timeout = 10000L;
		DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>( timeout );
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				Boolean result = (Boolean) deferredResult.getResult();
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), result);
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
					sendData( loginToken );
					deferredResult.setResult( ResponseEntity.ok().build() );
				} catch (Exception e) {
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() );
				}
			}
		});
		
		return deferredResult;
	}
	
	private void sendData(String loginToken) throws Exception {
		String url = ""; // logout
		String request = loginToken;
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
			// error
			e.printStackTrace();
			
			if ( logger.isErrorEnabled() ) {
				logger.error( "logout failed! loginToken:{}", loginToken );
			}
			throw new Exception( AppBaseResponse.FAIL.getCode() );
		}
	}
	

}
