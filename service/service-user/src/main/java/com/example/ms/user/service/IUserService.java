package com.example.ms.user.service;

import com.example.ms.user.domain.User;


public interface IUserService {
	
	User findById(long id);
	
	/** 
	 * 根据名称返回，支持登录名称，email和手机号
	 * @param name 可以是 login name, email or mobile 
	 * @return
	 */
	User findByName( String name );
	
	/**
	 * 检查是否存在
	 * @param name
	 * @return
	 */
	boolean exists( String name );
	
	User[] listByIds(long... ids);

}
