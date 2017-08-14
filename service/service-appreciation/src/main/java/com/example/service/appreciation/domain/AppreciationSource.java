package com.example.service.appreciation.domain;

import lombok.Getter;
import lombok.Setter;

public enum AppreciationSource {
	
	SELF(1),
	
	PARTY(2);
	
	@Setter @Getter
	private int value;
	
	AppreciationSource(int value) {
		this.value = value;
	}
}
