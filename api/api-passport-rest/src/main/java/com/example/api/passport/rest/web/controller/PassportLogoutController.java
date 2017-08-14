package com.example.api.passport.rest.web.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.common.plaindata.account.MsAccountLogoutRequest;
import com.example.common.plaindata.account.MsAccountLogoutResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;

@RestController
public class PassportLogoutController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AsyncTaskExecutor executor;
	
	@PostMapping(value="/passport/logout", produces="application/json;charset=utf-8")
	public DeferredResult<ResponseEntity<Void>> handle( @RequestHeader("accessToken") String accessToken, Locale locale ) {
		
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
					sendData( accessToken );
					deferredResult.setResult( ResponseEntity.ok().build() );
				} catch (Exception e) {
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() );
				}
			}
		});
		
		return deferredResult;
	}
	
	private void sendData(String accessToken) throws Exception {
		String url = "";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsAccountLogoutRequest requestBody = new MsAccountLogoutRequest( accessToken );
		Class<MsAccountLogoutResponse> responseType = MsAccountLogoutResponse.class;
		
		RequestEntity<MsAccountLogoutRequest> requestEntity = null;
		ResponseEntity<MsAccountLogoutResponse> responseEntity = null;
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
