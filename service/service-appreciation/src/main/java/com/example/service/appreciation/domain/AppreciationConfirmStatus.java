package com.example.service.appreciation.domain;

import lombok.Getter;
import lombok.Setter;

public enum AppreciationConfirmStatus {
	INIT(1),
	
	SUCCESS(2),
	
	FAIL(3);
	
	@Setter @Getter
	private int value;
	
	AppreciationConfirmStatus(int value) {
		this.value = value;
	}
}
