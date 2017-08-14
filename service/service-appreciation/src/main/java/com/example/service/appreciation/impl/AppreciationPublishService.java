package com.example.service.appreciation.impl;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.common.exception.appreciation.AppreciationCannotBeInHelpersAndHelpeesException;
import com.example.common.exception.appreciation.AppreciationCreatorInHelpersException;
import com.example.common.exception.appreciation.AppreciationException;
import com.example.service.appreciation.IAppreciationPublishService;
import com.example.service.appreciation.domain.AppreciationConfirm;
import com.example.service.appreciation.domain.AppreciationConfirmRepository;
import com.example.service.appreciation.domain.AppreciationConfirmResult;
import com.example.service.appreciation.domain.AppreciationConfirmStatus;
import com.example.service.appreciation.domain.AppreciationRepository;
import com.example.service.appreciation.domain.AppreciationSource;
import com.example.service.appreciation.domain.AppreciationVo;
import com.example.service.appreciation.message.AppreciationPublishMessageSender;

@Service
public class AppreciationPublishService implements IAppreciationPublishService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AppreciationRepository appreciationRepository;
	
	@Autowired
	private AppreciationConfirmRepository appreciationConfirmRepository;
	
	@Autowired
	private AppreciationPublishMessageSender messageSender;

	/* (non-Javadoc)
	 * @see com.example.service.appreciation.IAppreciationService#create(com.example.service.appreciation.domain.AppreciationVo)
	 */
	@Override
	public void create( AppreciationVo appr )
			throws AppreciationException {
		
		checkCreateLogic( appr );
		
		try {
			appreciationRepository.save( appr );
			
			// 自己创建的，直接同意
			if ( appr.getSource()==AppreciationSource.SELF.getValue() ) {
				AppreciationConfirm confirmItem = new AppreciationConfirm( appr.getId(), appr.getCreateBy(), AppreciationConfirmResult.AGREE );
				appreciationConfirmRepository.save( confirmItem  ); 
			}
			
			sendCreatedMessage( appr );
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// 此处有哪些错误？？？
			if ( e instanceof DataAccessException ) {
				
			}
			else {
				
			}
		}
	}
	
	@Override
	public void agree(AppreciationVo appr, long userId)
			throws AppreciationException {
		
		checkAgreeLogic( appr, userId );
		
		boolean userInHelpees = ArrayUtils.contains( appr.getFinalHelpeeIds(), userId );
		boolean userInHelpers = !userInHelpees;
		
		// TODO 添加确认项
		AppreciationConfirm confirmItem = new AppreciationConfirm( appr.getId(), userId, AppreciationConfirmResult.AGREE );
		appreciationConfirmRepository.save( confirmItem  ); 
		
		long len1 = appr.getFinalHelperIds().length, len2 = appr.getFinalHelpeeIds().length;
		long len = appreciationConfirmRepository.countByAppreciationIdAndConfirmResult( appr.getId(), AppreciationConfirmResult.AGREE );
		if ( userInHelpers ) {
			if ( len == (len1+len2) ) { // helpers finished 
				appr.setConfirmStatus( AppreciationConfirmStatus.SUCCESS );
				appreciationRepository.save( appr );
				
				sendAllHelpersConfirmFinishedMessage( appr );
			}
			else {
				// do nothing
			}
		}
		else {
			if ( len == len2 ) { // helpees finished 
				sendAllHelpeesConfirmFinishedMessage( appr );
			}
			else {
				// do nothing
			}
		}
		
	}
	@Override
	public void deny(AppreciationVo appr, long userId, String reason)
			throws AppreciationException {
		
		checkDenyLogic( appr, userId, reason );
		
		boolean userInHelpees = ArrayUtils.contains( appr.getFinalHelpeeIds(), userId );
		if ( userInHelpees ) {
			appr.setFinalHelpeeIds(ArrayUtils.removeElements( appr.getFinalHelpeeIds(), userId ));
			
		}
		else {
			appr.setFinalHelperIds(ArrayUtils.removeElements( appr.getFinalHelperIds(), userId ));
		}
		if ( 0==appr.getFinalHelpeeIds().length || 0==appr.getFinalHelperIds().length ) {
			appr.setConfirmStatus( AppreciationConfirmStatus.FAIL );
		}
		appreciationRepository.save( appr ); 
		
		// TODO 添加确认项
		AppreciationConfirm confirmItem = new AppreciationConfirm( appr.getId(), userId, AppreciationConfirmResult.DENY );
		appreciationConfirmRepository.save( confirmItem  ); 
		
		// 确定失败了
		if ( appr.isFail() ) {
			List<AppreciationConfirm> confirmList = appreciationConfirmRepository.listByAppreciationIdAndConfirmResult( appr.getId(), AppreciationConfirmResult.AGREE );
			int size = confirmList.size();
			Long[] agreeUserIds = new Long[size];
			for ( int i=0;i<size;i++ ) {
				agreeUserIds[i] = confirmList.get(i).getUserId();
			}
			sendFailedMessage( appr, agreeUserIds );
		}
	}

	/**
	 * 发送helpees确认完成的消息
	 * @param appr
	 */
	private void sendAllHelpeesConfirmFinishedMessage( AppreciationVo appr ) {
		messageSender.sendAllHelpeesConfirmFinishedMessage( appr, appr.getHelperIds() ); 
	}
	
	private void sendAllHelpersConfirmFinishedMessage( AppreciationVo appr ) {
		messageSender.sendAllHelpersConfirmFinishedMessage( appr ); 
	}
	
	private void sendFailedMessage( AppreciationVo appr, Long[] agreeUserIds ) {
		messageSender.sendFailedMessage( appr, agreeUserIds ); 
	}
	
	/**
	 * 创建时的消息
	 * 
	 * 1 非第三方创建，分为2种1情况
	 * 1.1 helpee就1人，必然是创建人，直接发送helper的通知；
	 * 1.2 helpee为多人，移除创建人后，发送helpee的通知
	 * 
	 * 2 第三方创建，直接发送helpee的通知
	 * @param appr
	 */
	private void sendCreatedMessage(AppreciationVo appr) {
		if ( appr.getSource()==AppreciationSource.PARTY.getValue() ) {
			messageSender.sendCreatedMessage( appr, appr.getHelpeeIds() );
			return;
		}
		
		if ( appr.getSource()==AppreciationSource.SELF.getValue() ) {
			if ( 1==appr.getHelpeeIds().length ) { // 仅仅1个用户，就是创建者本人 
				sendAllHelpeesConfirmFinishedMessage( appr );
				return;
			}
			
			Long[] helpeeIds = ArrayUtils.subarray( appr.getHelpeeIds(), 0, appr.getHelpeeIds().length );
			helpeeIds = ArrayUtils.removeElement( helpeeIds, appr.getCreateBy() );
			messageSender.sendCreatedMessage( appr, helpeeIds ); 
		}
	}


	private void checkCreateLogic( AppreciationVo appr ) throws AppreciationException {
		
		// a user can't be in both
		for ( Long helpeeId : appr.getHelpeeIds() ) {
			for ( Long helperId : appr.getHelperIds() ) {
				if ( helpeeId.longValue() == helperId.longValue() ) {
					throw new AppreciationCannotBeInHelpersAndHelpeesException();
				}
			}
		}
		
		// creatBy not in 
		for ( Long helperId : appr.getHelperIds() ) {
			if ( helperId.longValue() == appr.getCreateBy().longValue() ) {
				throw new AppreciationCreatorInHelpersException();
			}
		}
	}


	

	private void checkAgreeLogic(AppreciationVo appr, long userId) {
		boolean userValid = ArrayUtils.contains(appr.getFinalHelpeeIds(), userId) ||
			ArrayUtils.contains(appr.getFinalHelperIds(), userId);
		Assert.state( userValid, "" );
	}


	private void checkDenyLogic(AppreciationVo appr, long userId, String reason) {
		// TODO Auto-generated method stub
		
	}

}
