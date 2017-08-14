package com.example.ms.account.rest.web.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.example.common.exception.account.AccountException;
import com.example.common.plaindata.account.AccountCreateRequest;
import com.example.common.plaindata.account.AccountCreateRequestBody;
import com.example.common.plaindata.account.AccountCreateResp;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.ms.account.service.IAccountService;

@RestController
public class AccountCreateRestController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AsyncTaskExecutor executor;
	
	@Autowired
	private IAccountService accountService;
	
	@PutMapping(value="/accounts", produces="application/json")
	public DeferredResult<ResponseEntity<AccountCreateResp>> create( @RequestBody AccountCreateRequest requestData ) {
		long timeout = 15000L;
		DeferredResult<ResponseEntity<AccountCreateResp>> deferredResult = new DeferredResult<>( timeout );
		
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
				AccountCreateResp resp = new AccountCreateResp();
				resp.setMessage( AppBaseResponse.TIMEOUT.getCode() ); 
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(resp) );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData( requestData.getBody() );
					
					AccountCreateResp resp = new AccountCreateResp();
					resp.setCode( 0 );
					resp.setMessage( "success" );
					deferredResult.setResult( ResponseEntity.ok().body(resp) );
				} catch (Exception e) {
					AccountCreateResp resp = new AccountCreateResp();
					resp.setCode( 1 );
					
					// fail
					if ( e instanceof AccountException ) {
						resp.setMessage( ((AccountException)e).getCode() );
					}
					// error
					else {
						resp.setMessage( "error" );
					}
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp) );
				}
			}
		});
		
		return deferredResult;
	}
	
	private void sendData(AccountCreateRequestBody body) throws AccountException {
		
		// check input
		checkInput( body );
		
		// invoke service
		invokeService( body );
		
	}

	private void checkInput(AccountCreateRequestBody body) throws AccountException {
		// TODO Auto-generated method stub
		
	}

	private void invokeService(AccountCreateRequestBody body) throws AccountException {
		accountService.regist( body );
	}
}