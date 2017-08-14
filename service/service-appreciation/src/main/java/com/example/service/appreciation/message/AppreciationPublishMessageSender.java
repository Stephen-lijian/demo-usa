package com.example.service.appreciation.message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.example.service.appreciation.domain.AppreciationVo;

@Component
public final class AppreciationPublishMessageSender {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private AmazonSNS amazonSns;
	
	private String apprCreateArn = "";

	private String apprFailedArn = "";
	
	private String apprAllHelpeesConfirmArn = "";
	
	private String apprAllHelpersConfirmArn = "";
	
	
	public String sendCreatedMessage( final AppreciationVo appr, final Long[] helpeeIds ) {
		String topicArn = apprCreateArn;
		String message = "appr-create";
		
		// how to handle reject
		PublishRequest request = new PublishRequest( topicArn, message );
		MessageAttributeValue value = new MessageAttributeValue();
		value.setDataType( "String" );
		value.setStringValue( StringUtils.join(helpeeIds,",") );
		request.addMessageAttributesEntry( "", value );
		PublishResult result = amazonSns.publish(request);
		return result.getMessageId();
	}
	
	public String sendAllHelpeesConfirmFinishedMessage( final AppreciationVo appr, final Long[] helperIds ) {
		String topicArn = apprAllHelpeesConfirmArn;
		String message = "appr-helpee-confirm-finished";
		
		// how to handle reject
		PublishRequest request = new PublishRequest( topicArn, message );
		MessageAttributeValue value = new MessageAttributeValue();
		value.setDataType( "String" );
		value.setStringValue( StringUtils.join(helperIds,",") );
		request.addMessageAttributesEntry( "helperIds", value );
		PublishResult result = amazonSns.publish(request);
		return result.getMessageId();
	}
	
	public String sendAllHelpersConfirmFinishedMessage( final AppreciationVo appr ) {
		String topicArn = apprAllHelpersConfirmArn;
		String message = "appr-helper-confirm-finished";
		
		// how to handle reject
		PublishRequest request = new PublishRequest( topicArn, message );
		MessageAttributeValue value = new MessageAttributeValue();
		value.setDataType( "String" );
		value.setStringValue( appr.getId()+"" );
		request.addMessageAttributesEntry( "apprId", value );
		PublishResult result = amazonSns.publish( request );
		return result.getMessageId();
	}
	
	public String sendFailedMessage( final AppreciationVo appr, final Long[] agreeUserIds ) {
		String topicArn = apprFailedArn;
		String message = "appr-failed";
		
		// how to handle reject
		PublishRequest request = new PublishRequest( topicArn, message );
		MessageAttributeValue value = new MessageAttributeValue();
		value.setDataType( "String" );
		value.setStringValue( StringUtils.join(agreeUserIds,",") );
		request.addMessageAttributesEntry( "userIds", value );
		PublishResult result = amazonSns.publish(request);
		return result.getMessageId();
	}
	
}