package com.example.service.appreciation.domain;

import lombok.Getter;
import lombok.Setter;

public enum AppreciationConfirmResult {
	CREATE(1),
	
	AGREE(2),
	
	DENY(3);
	
	@Setter @Getter
	private int value;
	
	AppreciationConfirmResult(int value) {
		this.value = value;
	}
}
