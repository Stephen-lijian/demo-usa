package com.example.api.sample.rest.web.controller;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.example.api.sample.rest.Application;

@RestController
public class AsyncRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@RequestMapping(value="/webasyncTask", method = RequestMethod.GET)
	public WebAsyncTask<String> webasyncTask() {
		logger.info("invoke thread id is : {}", Thread.currentThread().getId());
		
		Callable<String> callable = new Callable<String>() {
	        public String call() throws Exception {
	            Thread.sleep(3000); //假设是一些长时间任务
	            return "result";
	        }
	    };
	    
	    long timeout = 100; // 30 * 1000
		WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(timeout, callable);
	    webAsyncTask.onCompletion( new Runnable(){
			@Override
			public void run() {
				logger.info("completion thread id is : {} ", Thread.currentThread().getId() );
			}
		});
	    webAsyncTask.onTimeout( new Callable<String>(){
			@Override
			public String call() throws Exception {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
				return "timeout";
			}
	    });	    
	    return webAsyncTask;
	}
	
	@RequestMapping(value="/asynctask", method = RequestMethod.GET)
	public DeferredResult<String> asyncTask(){
		DeferredResult<String> deferredResult = new DeferredResult<>();
		logger.info("invoke thread id is : {}", Thread.currentThread().getId());
		
		
		deferredResult.onCompletion( new Runnable(){
			@Override
			public void run() {
				String result = (String) deferredResult.getResult();
				logger.info("completion thread id is : {}, result:{}", Thread.currentThread().getId(), result);
			}
		}); 
		
		deferredResult.onTimeout( new Runnable(){
			@Override
			public void run() {
				logger.info("timeout thread id is : {} ", Thread.currentThread().getId() );
			}
		});
		
		new Thread( new Runnable(){
			@Override
			public void run() {
				int result = 0;
				for ( int i=0; i<100000; i++ ) {
					result += i;
				}
				deferredResult.setResult("result:"+result);
				
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
			}
		}).start();
		
		return deferredResult;
	}

}
