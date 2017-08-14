package com.example.api.account.rest.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.account.rest.web.response.ApiAccountLoginCaptchaResp;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountLoginCaptchaController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	@GetMapping(value="/accounts/loginCaptcha", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAccountLoginCaptchaResp>> loginCaptcha( @RequestHeader("os") String os, @RequestHeader("deviceId") String deviceId, Locale locale )  {
		long timeout = 15000L;
		
		DeferredResult<ResponseEntity<ApiAccountLoginCaptchaResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiAccountLoginCaptchaResp resp = new ApiAccountLoginCaptchaResp();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					String captcha = sendData( os, deviceId );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiAccountLoginCaptchaResp resp = new ApiAccountLoginCaptchaResp();
					resp.setMessage( message ); 
					resp.setCaptcha( captcha );
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiAccountLoginCaptchaResp resp = new ApiAccountLoginCaptchaResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}

	private String sendData(String os, String deviceId) throws Exception {
		// FIXME 
		Assert.hasText( os, "header param error" );
		Assert.hasText( deviceId, "header param error" );
		
		// write cache
		return "testcaptcha";
	}
	
}
