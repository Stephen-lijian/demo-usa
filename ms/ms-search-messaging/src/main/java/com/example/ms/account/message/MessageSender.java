package com.example.ms.account.message;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

@Component
public final class MessageSender {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostConstruct
	public void init() {
		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());		                           
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		String topicArn = "topicArn";
		
		// 订阅
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "sqs", "arn of sql");
		SubscribeResult subscribeResult = snsClient.subscribe(subRequest);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "subscribe : {}", subscribeResult.getSubscriptionArn() );
		}
	}
	
	public void sendMessage() {
		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());		                           
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		// 发布
		String topicArn = "topicArn";
		String msg = "msg";
		PublishRequest publishRequest = new PublishRequest(topicArn , msg );
		PublishResult publishResult = snsClient.publish(publishRequest);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "message send, id: {}", publishResult.getMessageId() );
		}
	}

}
