package com.example.service.appreciation.domain;

import lombok.Data;

@Data
public class AppreciationConfirm {
	
	private Long id;

	private Long appreciationId;
	
	private Long userId;
	
	private Long at;
	
	private AppreciationConfirmResult confirmResult;
	
	private AppreciationConfirmType confirmType = AppreciationConfirmType.SELF;

	public AppreciationConfirm(Long appreciationId, Long userId,
			AppreciationConfirmResult confirmResult) {
		this( appreciationId, userId, confirmResult, AppreciationConfirmType.SELF );
	}

	public AppreciationConfirm(Long appreciationId, Long userId,
			AppreciationConfirmResult confirmResult,
			AppreciationConfirmType confirmType) {
		this.appreciationId = appreciationId;
		this.userId = userId;
		this.confirmResult = confirmResult;
		this.confirmType = confirmType;
		
		this.at = System.currentTimeMillis();
	}
}
