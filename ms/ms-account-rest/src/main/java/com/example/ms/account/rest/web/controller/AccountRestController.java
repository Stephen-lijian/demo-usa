package com.example.ms.account.rest.web.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.exception.account.AccountException;
import com.example.common.plaindata.account.AccountCreateRequest;
import com.example.common.plaindata.account.AccountCreateRequestBody;
import com.example.common.plaindata.account.AccountCreateResp;
import com.example.common.plaindata.account.AccountLoginRequest;
import com.example.common.plaindata.account.AccountLoginRequestBody;
import com.example.common.plaindata.account.AccountLoginResp;
import com.example.ms.account.service.IAccountService;

@RestController
public class AccountRestController {
	
	private final Logger logger = LoggerFactory.getLogger( getClass() );
	
	@Autowired
	private IAccountService accountService;
	
	@PutMapping(value="/accounts", produces="application/json")
	public ResponseEntity<AccountCreateResp> create( @RequestBody AccountCreateRequest requestData ) {
		
		AccountCreateRequestBody body = requestData.getBody();
		
		try {
			// success
			accountService.regist( body );
			AccountCreateResp resp = new AccountCreateResp();
			resp.setCode( 0 );
			resp.setMessage( "success" );
			return ResponseEntity.ok().body(resp);
		} catch (Exception e) {
			// fail
			if ( e instanceof AccountException ) {
				AccountCreateResp resp = new AccountCreateResp();
				resp.setCode( 1 );
				resp.setMessage( ((AccountException)e).getCode() );
				return ResponseEntity.ok().body(resp);
			}
			
			// error
			AccountCreateResp resp = new AccountCreateResp();
			resp.setCode( 1 );
			resp.setMessage( "error" );
			return ResponseEntity.ok().body(resp);
		}
	}
	
	@PostMapping(value="/accounts/login", produces="application/json")
	public ResponseEntity<AccountLoginResp> login( @RequestBody AccountLoginRequest requestData ) {
		
		AccountLoginRequestBody body = requestData.getBody();
		
		try {
			// success
			String loginToken = accountService.login( body.getName(), body.getPassword() );
			AccountLoginResp resp = new AccountLoginResp();
			resp.setResult( loginToken );
			
			resp.setCode( 0 );
			resp.setMessage( "success" );
			return ResponseEntity.ok().body(resp);
		} catch (Exception e) {
			// fail
			if ( e instanceof AccountException ) {
				AccountLoginResp resp = new AccountLoginResp();
				resp.setCode( 1 );
				resp.setMessage( ((AccountException)e).getCode() );
				return ResponseEntity.ok().body(resp);
			}
			
			// error
			AccountLoginResp resp = new AccountLoginResp();
			resp.setCode( 1 );
			resp.setMessage( "error" );
			return ResponseEntity.ok().body(resp);
		}
	}
	
	
}