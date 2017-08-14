package com.example.api.passport.rest.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.example.api.passport.rest.core.PassportGenerator;

@Configuration
@Import({
	com.example.api.base.config.AppConfig.class
})
public class AppConfig {
	
	@Bean
	public PassportGenerator passportGenerator() {
		PassportGenerator generator = new PassportGenerator();
		return generator;
	}

}
