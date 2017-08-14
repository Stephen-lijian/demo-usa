package com.example.api.map.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapRestController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	

	@GetMapping(value="/map/appreciationMap", produces="application/json")
	public void getAppreciationMap()  {
		
	}
}
