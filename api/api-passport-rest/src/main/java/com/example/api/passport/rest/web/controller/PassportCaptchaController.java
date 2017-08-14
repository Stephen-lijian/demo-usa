package com.example.api.passport.rest.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.base.plaindata.ApiSimpleResponse;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class PassportCaptchaController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	
	/**
	 * 
	 * @param os
	 * @param deviceId
	 * @param utype	1-注册，2-登录
	 * @param locale
	 * @return
	 */
	@GetMapping(value="/passport/captcha/{utype}", produces="application/json")
	public DeferredResult<ResponseEntity<ApiSimpleResponse>> handleRequest( 
		@RequestHeader("os") String os, @RequestHeader("deviceId") String deviceId, 
		@PathVariable int utype,
		Locale locale )  {
		long timeout = 15000L;
		
		DeferredResult<ResponseEntity<ApiSimpleResponse>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiSimpleResponse resp = new ApiSimpleResponse();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				ApiSimpleResponse resp = new ApiSimpleResponse();
				try {
					execute( os, deviceId, utype, resp );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}

	private void execute( String os, String deviceId, int utype, ApiSimpleResponse resp ) throws Exception {
		// FIXME 
		Assert.hasText( os, "header param error" );
		Assert.hasText( deviceId, "header param error" );
		
		// write cache
		
	}
	
}
