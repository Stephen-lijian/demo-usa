package com.example.api.sample.rest.web.controller;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@RestController
public class LocaleRestController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	
	@PostConstruct
	public void init() {
		messageSource.setCacheSeconds( 3600 );
		messageSource.setBasename( "com/example/api/sample/rest/resp" );
	}
	
	@RequestMapping(value="/locale", method = RequestMethod.GET,produces="application/json;charset=utf-8")
	public ResponseEntity<String> locale(Locale locale, HttpServletRequest request) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		logger.info( "localeResolver: {}", localeResolver.getClass().getCanonicalName() );
		
		String key = "app.resp.success";
		String message = messageSource.getMessage( key, new Object[0], locale );
		logger.info( "locale:{}, key:{}, message:{}", locale.toString(), key, message );
		
		ResponseEntity<String> entity = ResponseEntity.ok().body("abc:"+locale.getCountry()+":"+locale.getLanguage()+":"+message);
		return entity;
	}

}
