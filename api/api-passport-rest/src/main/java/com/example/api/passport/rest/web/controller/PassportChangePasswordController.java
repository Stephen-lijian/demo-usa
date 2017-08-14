package com.example.api.passport.rest.web.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriTemplate;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.passport.rest.web.plaindata.ApiPassportChangePasswordRequest;
import com.example.api.passport.rest.web.plaindata.ApiPassportChangePasswordResp;
import com.example.api.passport.rest.web.plaindata.ApiPassportLoginResp;
import com.example.common.exception.account.AccountPasswordNotEqualException;
import com.example.common.plaindata.account.MsAccountChangePasswordRequest;
import com.example.common.plaindata.account.MsAccountChangePasswordResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;

@RestController
public class PassportChangePasswordController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	
	@PostMapping(value="/passport/changePassword", produces="application/json")
	public DeferredResult<ResponseEntity<ApiPassportChangePasswordResp>> handleRequest( 
			@RequestHeader("loginToken") String loginToken, ApiPassportChangePasswordRequest requestParam, Locale locale)  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiPassportChangePasswordResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiPassportLoginResp resp = new ApiPassportLoginResp();
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
					execute( requestParam );
					
					String message = messageSource.getMessage( AppBaseResponse.SUCCESS.getCode(), new Object[0], locale);
					ApiPassportChangePasswordResp resp = new ApiPassportChangePasswordResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiPassportChangePasswordResp resp = new ApiPassportChangePasswordResp();
					resp.setMessage( message ); 
					resp.setResult(false); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}


	private void execute(ApiPassportChangePasswordRequest requestParam) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		// post changePassword
		postChangePassword( requestParam );
	}


	private void checkValid(ApiPassportChangePasswordRequest requestParam) throws Exception {
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


	private void postChangePassword(ApiPassportChangePasswordRequest requestParam) throws Exception {
		String url = "";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsAccountChangePasswordRequest requestBody = new MsAccountChangePasswordRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		Class<MsAccountChangePasswordResponse> responseType = MsAccountChangePasswordResponse.class;
		
		RequestEntity<MsAccountChangePasswordRequest> requestEntity = null;
		ResponseEntity<MsAccountChangePasswordResponse> responseEntity = null;
		try {
			requestEntity = RequestEntity.post( uri ).accept( MediaType.APPLICATION_JSON_UTF8 ).body( requestBody );
			responseEntity = restTemplate.exchange( requestEntity, responseType );
			
			// success
			if ( 200 == responseEntity.getStatusCodeValue() ) {
				return;
			}
			
			// fail
			throw new Exception( responseEntity.getHeaders().getFirst( MsServiceConstant.HEADER_EXCEPTION ) );
		} catch (Exception e) {
			e.printStackTrace();
			
			// error
			throw new Exception( AppBaseResponse.FAIL.getCode() );
		}	
	}
	
}
