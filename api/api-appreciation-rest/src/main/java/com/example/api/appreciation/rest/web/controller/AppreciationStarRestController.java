package com.example.api.appreciation.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.appreciation.rest.Application;

@RestController
public class AppreciationStarRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	
	@PutMapping(value="/appreciations/{id}/star", produces="application/json")
	@PostMapping(value="/appreciations/{id}/star", produces="application/json")
	public void star(@PathVariable Long id) {
	
	}
	
	@PutMapping(value="/appreciations/{id}/unstar", produces="application/json")
	@PostMapping(value="/appreciations/{id}/unstar", produces="application/json")
	public void unstar(@PathVariable Long id) {
	
	}

}
