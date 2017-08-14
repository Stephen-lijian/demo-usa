package com.example.api.passport.rest.core;

import lombok.Getter;
import lombok.Setter;

public enum Platform {
	
	IOS(1),
	
	ANDROID(2);

	@Getter @Setter
	private int value;

	Platform(int value) {
		this.value = value;
	}
}
