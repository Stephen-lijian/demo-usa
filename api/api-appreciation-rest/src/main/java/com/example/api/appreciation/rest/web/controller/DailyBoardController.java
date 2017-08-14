package com.example.api.appreciation.rest.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.appreciation.rest.Application;

@RestController
public class DailyBoardController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@GetMapping(value="/dailyboard/all", produces="application/json")
	public void listAll() {
	
	}
	
	@GetMapping(value="/dailyboard/friends", produces="application/json")
	public void listFriends() {
	
	}

}
