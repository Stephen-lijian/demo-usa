package com.example.service.appreciation;


import com.example.common.exception.appreciation.AppreciationException;
import com.example.service.appreciation.domain.AppreciationVo;


public interface IAppreciationPublishService {
	
	void create( AppreciationVo appr ) throws AppreciationException;
	
	void agree( AppreciationVo appr, long userId ) throws AppreciationException;
	
	void deny( AppreciationVo appr, long userId, String reason ) throws AppreciationException;
	
}
