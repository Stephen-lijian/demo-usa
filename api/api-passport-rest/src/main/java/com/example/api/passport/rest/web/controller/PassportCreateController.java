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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriTemplate;

import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.api.passport.rest.web.plaindata.ApiPassportCreateRequest;
import com.example.api.passport.rest.web.plaindata.ApiPassportCreateResponse;
import com.example.common.exception.account.AccountNameIncorrectException;
import com.example.common.plaindata.account.MsAccountCreateRequest;
import com.example.common.plaindata.account.MsAccountCreateResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;

/**
 * 系统自动创建
 * @author zuozhengfeng
 *
 */
@RestController
public class PassportCreateController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	
	@PutMapping(value="/passport/create", produces="application/json")
	@PostMapping(value="/passport/create", produces="application/json")
	public DeferredResult<ResponseEntity<ApiPassportCreateResponse>> handle(ApiPassportCreateRequest requestParam, Locale locale )  {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<ApiPassportCreateResponse>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiPassportCreateResponse resp = new ApiPassportCreateResponse();
				resp.setMessage( message ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				ApiPassportCreateResponse resp = new ApiPassportCreateResponse();
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
	
	private void execute( ApiPassportCreateRequest requestParam, ApiPassportCreateResponse resp ) throws Exception {
		
		// invalid input
		checkValid( requestParam );
		
		// post create
		postCreate( requestParam, resp );
	}
	
	
	private void postCreate( ApiPassportCreateRequest requestParam, ApiPassportCreateResponse resp ) throws Exception {
		String url = "";
		
//		URI uri = new URI(url);
		
		Map<String,String> uriVariables = new HashMap<>();
		URI uri = new UriTemplate( url ).expand( uriVariables );
		
		MsAccountCreateRequest requestBody = new MsAccountCreateRequest();
		BeanUtils.copyProperties( requestParam, requestBody );
		Class<MsAccountCreateResponse> responseType = MsAccountCreateResponse.class;
		
		RequestEntity<MsAccountCreateRequest> requestEntity = null;
		ResponseEntity<MsAccountCreateResponse> responseEntity = null;
		try {
			requestEntity = RequestEntity.post( uri ).accept( MediaType.APPLICATION_JSON_UTF8 ).body( requestBody );
			responseEntity = restTemplate.exchange( requestEntity, responseType );
			
			// success
			if ( 200 == responseEntity.getStatusCodeValue() ) { 
				resp.setId( responseEntity.getBody().getId() );				
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

	private void checkValid( ApiPassportCreateRequest requestParam ) throws Exception {
		String name = requestParam.getName();
		if ( StringUtils.isEmpty(name) || name.length()<6 ) {
			throw new AccountNameIncorrectException();
		}
		
	}

}
