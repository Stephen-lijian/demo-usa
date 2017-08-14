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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.account.rest.web.request.ApiAccountChangePasswordRequest;
import com.example.api.account.rest.web.response.ApiAccountChangePasswordResp;
import com.example.api.account.rest.web.response.ApiAccountLoginResp;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.exception.account.AccountPasswordNotEqualException;
import com.example.common.plaindata.account.AccountChangePasswordRequest;
import com.example.common.plaindata.account.AccountChangePasswordRequestBody;
import com.example.common.plaindata.account.AccountChangePasswordResp;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountChangePasswordController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	
	@PostMapping(value="/accounts/changePassword", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAccountChangePasswordResp>> changePassword( 
			@RequestHeader("loginToken") String loginToken, ApiAccountChangePasswordRequest requestParam, Locale locale)  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiAccountChangePasswordResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build() );
			}
		});
		
		requestParam.setLoginToken( loginToken );
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData( requestParam );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiAccountChangePasswordResp resp = new ApiAccountChangePasswordResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiAccountChangePasswordResp resp = new ApiAccountChangePasswordResp();
					resp.setMessage( message ); 
					resp.setResult(false); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}


	private void sendData(ApiAccountChangePasswordRequest requestParam) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		// post changePassword
		postChangePassword( requestParam );
	}


	private void checkValid(ApiAccountChangePasswordRequest requestParam) throws Exception {
		// oldPassword
		
		// newPassword
		
		// rePassword equals newPassword
		if ( !requestParam.getNewPassword().equals(requestParam.getRePassword()) ) {
			throw new AccountPasswordNotEqualException();
		}
		
		// loginToken
		
		
		// userId
		
		// oldPassword valid
		
	}


	private void postChangePassword(ApiAccountChangePasswordRequest requestParam) throws Exception {
		String url = "";
		
		AccountChangePasswordRequestBody requestBody = new AccountChangePasswordRequestBody();
		BeanUtils.copyProperties( requestParam, requestBody );
		AccountChangePasswordRequest request = new AccountChangePasswordRequest(); 
		request.setBody(requestBody);
		
		Class<AccountChangePasswordResp> responseType = AccountChangePasswordResp.class;
		Map<String,String> uriVariables = new HashMap<>();
		
		try {
			ResponseEntity<AccountChangePasswordResp> entity = restTemplate.postForEntity( url, request, responseType, uriVariables );
			
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
	
}
