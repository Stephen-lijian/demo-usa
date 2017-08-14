package com.example.api.appreciation.generator.rest.web.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriTemplate;

import com.example.api.appreciation.generator.rest.web.plaindata.ApiAppreciationCreateResponse;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.plaindata.appreciation.MsAppreciationCreateRequest;
import com.example.common.plaindata.appreciation.MsAppreciationCreateResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;

@RestController
public class AppreciationDenyController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;

	@PutMapping(value="/appreciations/{id}/deny", produces="application/json")
	@PostMapping(value="/appreciations/{id}/deny", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAppreciationCreateResponse>> handleRequest(
			@PathVariable Long id, Locale locale )  {
		long timeout = 60000L;
		DeferredResult<ResponseEntity<ApiAppreciationCreateResponse>> deferredResult = new DeferredResult<>( timeout );
	
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
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ApiAppreciationCreateResponse(0L,"timeout")) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				ApiAppreciationCreateResponse resp = new ApiAppreciationCreateResponse();
				try {
					execute( id, resp );

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
	 
	
	private void execute( final Long id, final ApiAppreciationCreateResponse resp) throws Exception {
		
		checkValid( id );
		postDeny( id, resp );
	}


	private void checkValid( final Long id ) throws Exception {
		
	}

	private Long validateAndCreateAccountByName(String name) {
		return null;
	}


	private Boolean validateAccountById(Long id) {
		return null;
	}




	private void postDeny( final Long id, final ApiAppreciationCreateResponse resp) throws Exception {
		String url = "";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsAppreciationCreateRequest request = new MsAppreciationCreateRequest();
		//BeanUtils.copyProperties( requestParam, request );
		Class<MsAppreciationCreateResponse> responseType = MsAppreciationCreateResponse.class;
		
		RequestEntity<MsAppreciationCreateRequest> requestEntity = null;
		ResponseEntity<MsAppreciationCreateResponse> responseEntity = null;
		try {
			requestEntity = RequestEntity.post( uri ).accept( MediaType.APPLICATION_JSON_UTF8 ).body( request );
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
