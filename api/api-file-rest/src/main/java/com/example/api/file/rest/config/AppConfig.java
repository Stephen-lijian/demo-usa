package com.example.api.file.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
	com.example.common.file.config.AppConfig.class,
	com.example.api.base.config.AppConfig.class
})
public class AppConfig {

}
