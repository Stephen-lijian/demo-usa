package com.example.api.sample.rest.web.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.example.api.sample.rest.Application;

@RestController
public class SnsRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@RequestMapping(value="/sns", method = RequestMethod.GET)
	public DeferredResult<String> asyncTask(){
		long timeout = 3000L;
		DeferredResult<String> deferredResult = new DeferredResult<>(timeout);
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
				sendData();
				deferredResult.setResult("result:"+true);
				
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
			}
		}).start();
		
		return deferredResult;
	}
	
	@Autowired
    private AmazonSNS amazonSns;
	
	private void sendData() {
		
		String topicArn = "arn:aws:sns:us-east-1:927041730585:developer-sns";
		
		//publish to an SNS topic
		String msg = "api-sample-rest send Message, " + "中文" + ", "+ RandomStringUtils.randomAlphabetic(3);
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		MessageAttributeValue value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "valuevalue");
		publishRequest.addMessageAttributesEntry( "keykey", value  );
		PublishResult publishResult = amazonSns.publish(publishRequest);
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

}
