package com.example.api.account.rest.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.example.api.base.i18n.I18nBundleMessageSource;

@Configuration
@Import({
	com.example.api.base.config.AppConfig.class,
	com.example.common.file.Application.class
})
public class AppConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LocaleChangeInterceptor());
	}

	// may be common libs
	@Bean
	public RestTemplate restTemplate() {
		// FIXME 太细节的问题后面再研究
		HttpComponentsAsyncClientHttpRequestFactory requestFactory = new HttpComponentsAsyncClientHttpRequestFactory();
		requestFactory.setConnectionRequestTimeout( 600000 );
		requestFactory.setConnectTimeout( 600000 );
		requestFactory.setReadTimeout( 60000 );
		return new RestTemplate( requestFactory  );
	}
	
	@Bean
	@Primary
	public AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setDaemon( true ); // 待定
		executor.setMaxPoolSize( 200 );
		executor.setCorePoolSize(100);
		executor.setQueueCapacity( 1000000000 );
		executor.setThreadGroup( new ThreadGroup("task-executor") );
		executor.setWaitForTasksToCompleteOnShutdown( true );		
		return executor;
	}
	
	@Bean
	public I18nBundleMessageSource i18nBundleMessageSource() {
		return new I18nBundleMessageSource();
	}
	
}
