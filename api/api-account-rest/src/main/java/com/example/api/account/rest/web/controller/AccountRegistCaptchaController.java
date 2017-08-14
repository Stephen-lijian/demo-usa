package com.example.api.account.rest.web.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.api.account.rest.web.request.ApiAccountRegistCaptchaRequest;
import com.example.api.account.rest.web.response.ApiAccountRegistCaptchaResp;
import com.example.api.base.i18n.I18nBundleMessageSource;
import com.example.common.plaindata.base.AppBaseResponse;

@RestController
public class AccountRegistCaptchaController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private I18nBundleMessageSource messageSource;
	
	/** 请求注册验证码 */
	@GetMapping(value="/accounts/registCaptcha", produces="application/json")
	public DeferredResult<ResponseEntity<ApiAccountRegistCaptchaResp>> handle(ApiAccountRegistCaptchaRequest requestParam, Locale locale )  {
		long timeout = 15000L;
		
		DeferredResult<ResponseEntity<ApiAccountRegistCaptchaResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				ApiAccountRegistCaptchaResp resp = new ApiAccountRegistCaptchaResp();
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
					ApiAccountRegistCaptchaResp resp = new ApiAccountRegistCaptchaResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					String message = messageSource.getMessage( e.getMessage(), new Object[0], locale);
					ApiAccountRegistCaptchaResp resp = new ApiAccountRegistCaptchaResp();
					resp.setMessage( message ); 
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}

	private void sendData(ApiAccountRegistCaptchaRequest requestParam) throws Exception {
		
		//FIXME 自己写函数直接操作缓存完成
	}
	
}
