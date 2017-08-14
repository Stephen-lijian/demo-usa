package com.example.service.appreciation.domain;

import lombok.Getter;
import lombok.Setter;

public enum AppreciationConfirmType {
	SELF(1),
	
	TIMEOUT(2);
	
	@Setter @Getter
	private int value;
	
	AppreciationConfirmType(int value) {
		this.value = value;
	}
}
