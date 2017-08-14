package com.example.service.appreciation.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppConfig {
	
	@Bean(name="serviceExecutor")
	@Qualifier("serviceExecutor")
	@ConditionalOnMissingBean(name="serviceExecutor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setDaemon( true ); // 待定
		executor.setMaxPoolSize( 200 );
		executor.setCorePoolSize(100);
		executor.setQueueCapacity( 1000000000 );
		executor.setThreadGroup( new ThreadGroup("service-executor") );
		executor.setWaitForTasksToCompleteOnShutdown( true );		
		return executor;
	}

}
