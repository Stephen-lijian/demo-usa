package com.example.api.sample.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

//@RestController
public class SnsReceiveRestController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AsyncTaskExecutor executor;
	
	@RequestMapping(value="/snsr", method = RequestMethod.GET)
	public DeferredResult<ResponseEntity<String>> snsr(){
		long timeout = 600000L;
		DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>( timeout );
		
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
				deferredResult.setResult( ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build() );
			}
		});
		
		executor.submit( new Runnable(){
			@Override
			public void run() {
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
				
				try {
					sendData();
					deferredResult.setResult( ResponseEntity.ok().body("OK") );
				} catch (Exception e) {
					e.printStackTrace();
					deferredResult.setResult( ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAILURE") );
				}
			}
		});
		
		return deferredResult;
	}
	private void sendData() {
		//SimpleMessageListenerContainer c;
	}

	@SqsListener("dev-queue")
	public void queueListener1( Object obj ) {
		// ...
		logger.info( "queueListener1: {}", obj.toString() );
	}
	
	// 1个APP中不能出现2个方法监听1个队列
//	@SqsListener("dev-queue")
//	public void queueListener2( Object obj ) {
//		// ...
//		logger.info( "queueListener2: {}", obj.toString() );
//	}
}
