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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriTemplate;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.passport.rest.web.plaindata.ApiPassportLoginRequest;
import com.example.api.passport.rest.web.plaindata.ApiPassportLoginResp;
import com.example.common.plaindata.account.MsAccountLoginRequest;
import com.example.common.plaindata.account.MsAccountLoginResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;

@RestController
public class PassportLoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	@PostMapping(value="/passport/login", produces="application/json")
	public DeferredResult<ResponseEntity<ApiPassportLoginResp>> handle( ApiPassportLoginRequest requestParam, Locale locale ) {
		
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiPassportLoginResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				ApiPassportLoginResp resp = new ApiPassportLoginResp();
				try {
					sendData( requestParam, resp );
					
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
 
	private void sendData( ApiPassportLoginRequest requestParam, ApiPassportLoginResp resp ) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		postLogin( requestParam, resp );
	}

	private void postLogin(ApiPassportLoginRequest requestParam, ApiPassportLoginResp resp) throws Exception {
		String url = "/account/login";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsAccountLoginRequest requestBody = new MsAccountLoginRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		Class<MsAccountLoginResponse> responseType = MsAccountLoginResponse.class;
		
		RequestEntity<MsAccountLoginRequest> requestEntity = null;
		ResponseEntity<MsAccountLoginResponse> responseEntity = null;
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

	private void checkValid(ApiPassportLoginRequest requestParam) throws Exception {
		
		// name
		
		// password
		
		// captcha in cache
		
	}
	

}
