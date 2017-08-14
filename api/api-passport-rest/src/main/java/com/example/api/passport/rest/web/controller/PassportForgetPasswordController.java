package com.example.api.passport.rest.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.passport.rest.web.plaindata.ApiPassportForgetPasswordRequest;
import com.example.api.passport.rest.web.plaindata.ApiPassportForgetPasswordResp;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class PassportForgetPasswordController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	/**
	 * step = [input|validate|reset]
	 * @return
	 */
	@PostMapping(value="/passport/forgetPassword", params="step", produces="application/json")
	public DeferredResult<ResponseEntity<ApiPassportForgetPasswordResp>> handle( ApiPassportForgetPasswordRequest requestParam, Locale locale)  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiPassportForgetPasswordResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiPassportForgetPasswordResp resp = new ApiPassportForgetPasswordResp();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData( requestParam );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiPassportForgetPasswordResp resp = new ApiPassportForgetPasswordResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiPassportForgetPasswordResp resp = new ApiPassportForgetPasswordResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}

	private void sendData(ApiPassportForgetPasswordRequest requestParam) throws Exception {
		// invalid input
		checkValid( requestParam );
		
		// post 
		postForgetPassword( requestParam );
	}

	private void checkValid(ApiPassportForgetPasswordRequest requestParam) {
		
	}

	private void postForgetPassword(ApiPassportForgetPasswordRequest requestParam) {
		
	}

}
