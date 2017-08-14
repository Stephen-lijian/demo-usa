package com.example.ms.user.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ms.user.domain.User;
import com.example.ms.user.domain.UserRepository;
import com.example.ms.user.service.IUserService;

@Service
public class UserService implements IUserService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserRepository userRepository;

	@Override
	public User findById(long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public User findByName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public User[] listByIds(long... ids) {
		throw new UnsupportedOperationException();
	}
	



}
