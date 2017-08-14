package com.example.ms.appreciation.generator.web.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.exception.appreciation.AppreciationException;
import com.example.common.plaindata.appreciation.MsAppreciationCreateRequest;
import com.example.common.plaindata.appreciation.MsAppreciationCreateResponse;
import com.example.common.plaindata.base.AppBaseResponse;
import com.example.common.plaindata.base.MsServiceConstant;
import com.example.service.appreciation.IAppreciationPublishService;
import com.example.service.appreciation.domain.AppreciationVo;

@RestController
public class AppreciationRestController {

	@Autowired
	private IAppreciationPublishService appreciationService;

	@PutMapping(value="/appreciations", produces="application/json")
	public ResponseEntity<MsAppreciationCreateResponse> create( @RequestBody MsAppreciationCreateRequest requestData ) {
		
		MsAppreciationCreateResponse resp = new MsAppreciationCreateResponse();
		
		AppreciationVo appr = new AppreciationVo();
		BeanUtils.copyProperties( requestData, appr );
		
		Long[] dest = new Long[appr.getHelperIds().length];
		System.arraycopy(appr.getHelperIds(), 0, dest , 0, appr.getHelperIds().length);
		appr.setFinalHelperIds( dest );
		
		dest = new Long[appr.getHelpeeIds().length];
		System.arraycopy(appr.getHelpeeIds(), 0, dest , 0, appr.getHelpeeIds().length);
		appr.setFinalHelpeeIds( dest );
		
		try {
			// success
			appreciationService.create( appr );
			
			return ResponseEntity.ok().body(resp);
		} catch (Exception e) {
			// fail 
			if ( e instanceof AppreciationException ) {
				return ResponseEntity
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.header( MsServiceConstant.HEADER_EXCEPTION, ((AppreciationException)e).getCode() )
						.build();
			}
			
			// error
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.header( MsServiceConstant.HEADER_EXCEPTION, AppBaseResponse.FAIL.getCode() )
					.build();
		}
	}

}
