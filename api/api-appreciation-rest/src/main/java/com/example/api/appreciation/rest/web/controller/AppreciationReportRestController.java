package com.example.api.appreciation.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.appreciation.rest.Application;

@RestController
public class AppreciationReportRestController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@PutMapping(value="/appreciations/{appreciationId}/reports", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/reports", produces="application/json")
	public void createReport(@PathVariable Long appreciationId) {
		
	}
	
	@PutMapping(value="/appreciations/{appreciationId}/reports/{reportId}", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/reports/{reportId}", produces="application/json")
	public void updateReport(@PathVariable Long appreciationId, @PathVariable Long reportId) {
		
	}

	@PutMapping(value="/appreciations/{appreciationId}/reports/{reportId}/cancel", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/reports/{reportId}/cancel", produces="application/json")
	public void cancelReport(@PathVariable Long appreciationId, @PathVariable Long reportId) {
		
	}
	
	
	@PutMapping(value="/appreciations/{appreciationId}/antireports", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/antireports", produces="application/json")
	public void createAntireport(@PathVariable Long appreciationId) {
		
	}
	
	@PutMapping(value="/appreciations/{appreciationId}/antireports/{antireportId}", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/antireports/{antireportId}", produces="application/json")
	public void updateAntireport(@PathVariable Long appreciationId, @PathVariable Long antireportId) {
		
	}

	@PutMapping(value="/appreciations/{appreciationId}/antireports/{antireportId}/cancel", produces="application/json")
	@PostMapping(value="/appreciations/{appreciationId}/antireports/{antireportId}/cancel", produces="application/json")
	public void cancelAntireport(@PathVariable Long appreciationId, @PathVariable Long antireportId) {
		
	}
	
	@PutMapping(value="/appreciations/{id}/judge", produces="application/json")
	@PostMapping(value="/appreciations/{id}/judge", produces="application/json")
	public void judgeReport(@PathVariable Long id) {
		
	}
}
