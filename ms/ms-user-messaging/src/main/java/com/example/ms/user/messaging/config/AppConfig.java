package com.example.ms.user.messaging.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {
	
	@Bean
	@Qualifier("userMessagingExecutor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setDaemon( true ); // 待定
		executor.setMaxPoolSize( 200 );
		executor.setCorePoolSize( 100 );
		executor.setQueueCapacity( 1000000000 );
		executor.setThreadGroup( new ThreadGroup("user-messaging-executor") );
		executor.setWaitForTasksToCompleteOnShutdown( true );	
		return executor;
	}
	

}
