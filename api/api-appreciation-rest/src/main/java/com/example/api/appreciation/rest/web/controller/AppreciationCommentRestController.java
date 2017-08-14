package com.example.api.appreciation.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.appreciation.rest.Application;

@RestController
public class AppreciationCommentRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@PutMapping(value="/appreciations/{id}/comment", produces="application/json")
	@PostMapping(value="/appreciations/{id}/comment", produces="application/json")
	public void createComment(@PathVariable Long id) {
	
	}
	
	@PutMapping(value="/appreciations/{appreciationId}/comment/{commentId}", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/comment/{commentId}", produces="application/json")
	public void updateComment( @PathVariable Long appreciationId, @PathVariable Long commentId ) {
	
	}
	
	@DeleteMapping(value="/appreciations/{appreciationId}/comment/{commentId}", produces="application/json")
	public void deleteComment( @PathVariable Long appreciationId, @PathVariable Long commentId ) {
	
	}

}
