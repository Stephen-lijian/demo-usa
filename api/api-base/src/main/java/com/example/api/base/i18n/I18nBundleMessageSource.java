package com.example.api.base.i18n;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class I18nBundleMessageSource {
	
	private ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	
	@PostConstruct
	public void init() {
		messageSource.setCacheSeconds( 3600 );
		messageSource.addBasenames( "com/example/api/account/rest/resp" );
	}
	
	public final String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}
	
	public final String getMessage(String code, Object[] args, Locale locale) {
		return messageSource.getMessage(code, args, code, locale);
	}
}
