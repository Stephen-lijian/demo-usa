package com.example.ms.user.messaging.receiver;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import lombok.Data;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.ms.user.domain.User;
import com.example.ms.user.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserReceiver { 
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	@Qualifier("userMessagingExecutor")
	private AsyncTaskExecutor executor;
	
	@SuppressWarnings("unchecked")
	@SqsListener("dev-sqs-appr-create-notification")
	public void listener( String str ) {
		
		Map<String, Object> message = null;
		message = parseMessage( str );
		if ( message == null ) {
			if ( logger.isWarnEnabled() ) {
				logger.warn( "return because of invalid message, {}", str );
			}
			return;
		}
		
		Map<String,Object> attrs = (Map<String, Object>) message.get( "MessageAttributes");
		final long apprId = getLongField( "id", attrs );
		final String emailNotification = getStringField( "emailNotification", attrs );
		final String smsNotification = getStringField( "smsNotification", attrs );
		final String appNotification = getStringField( "appNotification", attrs );
		
		long[] helperIds = getHelperIds( attrs );
		long[] helpeeIds = getHelpeeIds( attrs );
		
		User[] helpers = userService.listByIds( helperIds );
		User[] helpees = userService.listByIds( helpeeIds );
		
		sendToUsers( helpers, apprId, emailNotification, smsNotification, appNotification );
		sendToUsers( helpees, apprId, emailNotification, smsNotification, appNotification );
		
	}

	private void sendToUsers(final User[] allUsers, final long apprId,
			final String emailNotification, final String smsNotification,
			final String appNotification) {
		final int pageSize = 3;
		int size = allUsers.length;
		
		for ( int i=0; i<size; i+=pageSize) {
			int start = i, end = i+pageSize;
			if ( end > size ) {
				end = size;
			}
			User[] users = ArrayUtils.subarray( allUsers, start, end );
			executor.submit( new SendNotificationTask(apprId, emailNotification, smsNotification, appNotification, users) );
		}
	}
	
	
	@Data
	class SendNotificationTask implements Callable<Void> {
		final long apprId;
		final String emailNotification;
		final String smsNotification;
		final String appNotification;
		final User[] users;

		@Override
		public Void call() throws Exception {
			// TODO Auto-generated method stub
			
			for ( User user : users ) {
				sendEmail( user, apprId, emailNotification );
				sendSms( user, apprId, smsNotification );
				sendApp( user, apprId, smsNotification );
			}
			
			return null;
		}

		private void sendEmail(User user, long apprId2,
				String emailNotification2) {
			// TODO Auto-generated method stub
			logger.debug( "send email ");
		}

		private void sendSms(User user, long apprId2, String smsNotification2) {
			// TODO Auto-generated method stub
			logger.debug( "send sms ");
		}

		private void sendApp(User user, long apprId2, String smsNotification2) {
			// TODO Auto-generated method stub
			logger.debug( "send app ");
		}
		
	}
	

	private long[] getHelperIds(Map<String, Object> attrs) {
		return getUserIds( "helperIds", attrs );
	}

	private long[] getHelpeeIds(Map<String, Object> attrs) {
		return getUserIds( "helpeeIds", attrs );
	}
	
	private long[] getUserIds( String field, Map<String, Object> attrs ) {
		String str = getStringField( field, attrs );
		String[] itemArr = StringUtils.splitByWholeSeparator( str, "," );
		long[] userIds = new long[ itemArr.length ];
		int i = 0;
		for ( String item : itemArr ) {
			userIds[i++] = Long.parseLong( item );
		}
		return userIds;
	}

	@SuppressWarnings("unchecked")
	private long getLongField(String key, Map<String, Object> attr) {
		try {
			Map<String,Object> valueMap = (Map<String, Object>) attr.get(key);
			String value = (String) valueMap.get("Value");
			return Long.parseLong( value );
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getStringField(String key, Map<String, Object> attr) {
		try {
			Map<String,Object> valueMap = (Map<String, Object>) attr.get(key);
			String value = (String) valueMap.get("Value");
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> parseMessage(String str) {
		ObjectMapper om = new ObjectMapper();
		try {
			Map<String, Object> map = om.readValue( str, Map.class );
			if ( !map.containsKey( "MessageAttributes") || 
			   ( null==map.get( "MessageAttributes") ) || 
		       !( map.get( "MessageAttributes") instanceof Map)) {
				return null;
			}
			return map;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
