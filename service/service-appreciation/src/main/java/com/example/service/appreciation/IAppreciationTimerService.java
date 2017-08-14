package com.example.service.appreciation;

import com.example.common.exception.appreciation.AppreciationException;

public interface IAppreciationTimerService {
	
	void addItem( long apprId, long endTime ) throws AppreciationException;

}
