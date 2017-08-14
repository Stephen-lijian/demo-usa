package com.example.ms.account.service;


import com.example.common.exception.account.AccountException;
import com.example.common.plaindata.account.AccountCreateRequestBody;


public interface IAccountService {
	
	void regist(AccountCreateRequestBody body) throws AccountException;
	
	String login(String name, String password)  throws AccountException;
	
	void logout( String token ) throws AccountException;
	
//	boolean changePassword( String name, String oldPassword, String newPassword );
//	
//	boolean[] exists( String... names );

}
