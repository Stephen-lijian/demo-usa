package com.example.ms.appreciation.messaging.receiver;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AppreciationGeneratorReceiver {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
//	@Autowired
//	private IAppreciationTimerService appreciationTimerService;
	
	/**
	 * receive message when appr created.
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	@SqsListener("dev-sqs-appr-create")
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
		long apprId = getLongField( "id", attrs );
		long createTime = getLongField( "createTime", attrs );
		
		final int hours = 3;
		long endTime = createTime + hours*(60*60*1000);
		
		logger.info( "id:{}, createTime:{}, endTime:{}", apprId, createTime, endTime );
		
//		try {
//			appreciationTimerService.addItem( apprId, endTime );
//		} catch (AppreciationException e) {
//			e.printStackTrace();
//		}
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
