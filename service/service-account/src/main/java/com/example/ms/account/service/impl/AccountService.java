package com.example.ms.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.common.exception.account.AccountException;
import com.example.common.exception.account.AccountNameNotFoundException;
import com.example.common.exception.account.AccountPasswordIncorrectException;
import com.example.common.message.account.AccountCreatedMessage;
import com.example.common.plaindata.account.MsAccountCreateRequest;
import com.example.ms.account.domain.Account;
import com.example.ms.account.domain.AccountRepository;
import com.example.ms.account.message.MessageSender;
import com.example.ms.account.service.IAccountService;

@Service
public class AccountService implements IAccountService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public void regist(MsAccountCreateRequest body) throws AccountException {
		
		String name = "";
		if ( StringUtils.isNotBlank(body.getEmail()) ) {
			name = body.getEmail();
		}
		if ( StringUtils.isNotBlank(body.getMobile()) ) {
			name = body.getMobile();
		}
		Assert.assertTrue( StringUtils.isNoneBlank(name) );
		
		Account account = new Account();
		BeanUtils.copyProperties( body, account ); 

		// 检查名称是否存在
		checkLogic(account);
		
		// 存入数据库
		try {
			Account accountSaved = accountRepository.save( account );
			if ( logger.isInfoEnabled() ) {
				logger.info( "account saved, id:{}, name: {}", accountSaved.getId(), name ); 
			}
			
			// FIXME 保存成功后发送消息，帐号创建成功
			// 后续动作：创建用户帐号，更新缓存；
			// 对于这种场景，应该统一在网关层处理?
			AccountCreatedMessage message = new AccountCreatedMessage();
			BeanUtils.copyProperties(account, message);
			sendMessage( message );
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// 此处有哪些错误？？？
			if ( e instanceof DataAccessException ) {
				
			}
			else {
				
			}
		}
		
	}
	

	@Autowired
	private MessageSender messagePublish;
	
	private void sendMessage(AccountCreatedMessage message) {
		messagePublish.sendMessage();
	}


	private void checkLogic(Account account) throws AccountException {
		// is account existed
	}



	@Override
	public String login(String name, String password) throws AccountException {
		
		// not found
		Account account = accountRepository.findByEmailOrMobile( name );
		if ( account == null ) {
			throw new AccountNameNotFoundException();
		}
		
		// password incorrect
		if ( !password.equals(account.getPassword())) {
			throw new AccountPasswordIncorrectException();
		}
		
		// too many logins
		
		// cache 
		String loginToken = "loginToken";
		
		// send message ?
		
		return loginToken;
	}

	@Override
	public void logout(String token) throws AccountException {
		// TODO Auto-generated method stub
	}



}
