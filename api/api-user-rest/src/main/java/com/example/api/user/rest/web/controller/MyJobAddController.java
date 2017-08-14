package com.example.api.user.rest.web.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriTemplate;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.base.plaindata.ApiRequest;
import com.example.api.base.plaindata.ApiResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsRequest;
import com.example.common.plaindata.base.MsResponse;
import com.example.common.plaindata.base.MsServiceConstant;

/**
 * 当前登录用户的信息
 * @author zuozhengfeng
 *
 */
@RestController
public class MyJobAddController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	
	@GetMapping(value="/users/profile", produces="application/json;charset=utf8")
	public DeferredResult<ResponseEntity<ApiResponse>> handleRequest(ApiRequest requestParam, Locale locale )  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiResponse>> deferredResult = new DeferredResult<>( timeout );
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				// logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), deferredResult.getResult() );
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				// logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				
				String message = messageSource.getMessage( AppBaseResponse.TIMEOUT.getCode(), new Object[0], locale);
				ApiResponse resp = new ApiResponse();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				// logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				ApiResponse resp = new ApiResponse();
				try {
					execute( requestParam, resp );
					
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
	
	private void execute( ApiRequest requestParam, ApiResponse resp ) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		// post regist
		getContent( requestParam, resp );
	}
	
	
	private void getContent( ApiRequest requestParam, ApiResponse resp ) throws Exception {
		String url = "";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsRequest requestBody = new MsRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		Class<MsResponse> responseType = MsResponse.class;
		
		RequestEntity<Void> requestEntity = null;
		ResponseEntity<MsResponse> responseEntity = null;
		try {
			requestEntity = RequestEntity.get( uri ).accept( MediaType.APPLICATION_JSON_UTF8 ).build();
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

	private void checkValid( ApiRequest requestParam ) throws Exception {
		

		// repassword
		
		// times limited or not 
	}

}
