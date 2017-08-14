package com.example.api.passport.rest.core;

import lombok.Getter;
import lombok.Setter;

public enum TokenType {
	
	DEFAULT(0);
	
	@Getter @Setter
	private int value;

	TokenType(int value) {
		this.value = value;
	}

}
