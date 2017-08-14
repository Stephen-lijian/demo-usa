package com.example.api.sample.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.sample.rest.web.request.MissingFieldsRequestParam;

@RestController
public class MissingFieldsRequestParamController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping(value="/missing", produces="application/json", consumes="application/json" ) // produces="application/json", 
	public ResponseEntity<String> handle( @RequestBody MissingFieldsRequestParam requestParam ) {
		ResponseEntity<String> entity = ResponseEntity.ok().body( requestParam.getMissing() );
		logger.info( requestParam.toString() );
		return entity;
	}

}
