package com.example.api.sample.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
//@EnableWebMvc
//@Import({
//	com.example.api.base.config.AppConfig.class,
//	com.example.common.file.Application.class
//})
@Configuration
@EnableWebMvc
@Import({
	com.example.api.base.config.AppConfig.class
})
public class AppConfig extends WebMvcConfigurerAdapter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		logger.error( "addInterceptors addInterceptors addInterceptors" );
		
		//registry.addInterceptor(new LocaleChangeInterceptor());
		
	}

	// may be common libs
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}
