package com.example.api.sample.rest.web.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.example.api.sample.rest.Application;

@RestController
public class MockApprCreateRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	
	@RequestMapping(value="/mockapprcreate", method = RequestMethod.GET)
	public DeferredResult<String> mockapprcreate(){
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
				sendData();
				deferredResult.setResult("result");
				logger.info("exec thread id is : {}", Thread.currentThread().getId());
			}
		}).start();
		
		return deferredResult;
	}
	
	private void sendData() {
		AmazonSNSClient snsClient = new AmazonSNSClient();		                           
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		String topicArn = "arn:aws:sns:us-east-1:927041730585:dev-sns-appr-create";
		
		//publish to an SNS topic
		String msg = "api-sample-rest send appr create, " + RandomStringUtils.randomAlphabetic(3);
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		publishRequest.setSubject("appr create");
		
		MessageAttributeValue value = null;
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "3,4,5" );		
		publishRequest.addMessageAttributesEntry( "helperIds", value  );
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "31,41,51" );		
		publishRequest.addMessageAttributesEntry( "helpeeIds", value  );
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "app notification" );		
		publishRequest.addMessageAttributesEntry( "appNotification", value  );
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "email notification" );		
		publishRequest.addMessageAttributesEntry( "emailNotification", value  );
		

		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "sms notification" );		
		publishRequest.addMessageAttributesEntry( "smsNotification", value  );
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( "1" );		
		publishRequest.addMessageAttributesEntry( "id", value  );
		
		value = new MessageAttributeValue();
		value.setDataType( "String") ;
		value.setStringValue( ""+System.currentTimeMillis() );		
		publishRequest.addMessageAttributesEntry( "createTime", value  );
		
		PublishResult publishResult = snsClient.publish( publishRequest );
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

}
