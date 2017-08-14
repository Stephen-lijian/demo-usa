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
public class AppreciationRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@GetMapping(value="/appreciations/{id}", produces="application/json")
	public void get(@PathVariable Long id) {
		
	}
	
	@PutMapping(value="/appreciations/{id}", produces="application/json")
	@PostMapping(value="/appreciations/{id}", produces="application/json")
	public void update(@PathVariable Long id) {
		
	}
	
	@PutMapping(value="/appreciations/{id}/removeHelpers", produces="application/json")
	@PostMapping(value="/appreciations/{id}/removeHelpers", produces="application/json")
	public void removeHelpers(@PathVariable Long id, @RequestBody Long[] helpers) {
		
	}
	
	@PutMapping(value="/appreciations/{id}/removeHelpees", produces="application/json")
	@PostMapping(value="/appreciations/{id}/removeHelpees", produces="application/json")
	public void removeHelpees(@PathVariable Long id, @RequestBody Long[] helpers) {
		
	}
	
	@GetMapping(value="/appreciations/{id}/aggree", produces="application/json")
	public void aggree(@PathVariable Long id) {
		
	}
	
	@GetMapping(value="/appreciations/{id}/reject", produces="application/json")
	public void reject(@PathVariable Long id) {
		
	}

}
