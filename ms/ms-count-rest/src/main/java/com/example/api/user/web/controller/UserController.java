package com.example.api.user.web.controller;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.user.Application;
import com.example.api.user.web.param.UserParam;
import com.example.microservice.user.rpcservice.User;
import com.example.microservice.user.rpcservice.UserService;

@RestController
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	private String serverIp = "127.0.0.1";
	private int serverPort = 8081;
	private int timeout = 3000;

	@GetMapping(value="/users/{id}", produces="application/json")
//	@GetMapping(value="/users/{id}") 
	public String get(@PathVariable Long id) throws TException {
		
		TTransport transport = new TFramedTransport(new TSocket(serverIp,serverPort,timeout));

        TProtocol protocol = new TCompactProtocol(transport);
        transport.open();

        // 0.9.1版本以上支持多服务接口共用实现,这里也必须保持和服务端一致
        TMultiplexedProtocol tpUserService = new TMultiplexedProtocol(protocol,"userService");
        UserService.Client client = new UserService.Client(tpUserService);
        User user;
		try {
			user = client.find(id);
			logger.debug("result : {}", user);
			return user.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "nouser";
		} finally {
			transport.close();
		}
	}
	
	@PutMapping(value="/users", produces="application/json")
	@PostMapping(value="/users", produces="application/json")
	public String add(@RequestBody UserParam userParam) throws TException {
		TTransport transport = new TFramedTransport(new TSocket(serverIp,serverPort,timeout));

        TProtocol protocol = new TCompactProtocol(transport);
        transport.open();

        // 0.9.1版本以上支持多服务接口共用实现,这里也必须保持和服务端一致
        TMultiplexedProtocol tpUserService = new TMultiplexedProtocol(protocol,"userService");
        UserService.Client client = new UserService.Client(tpUserService);
        User user = new User();
        BeanUtils.copyProperties(userParam, user); 
		try {
			client.save( user );
			logger.debug("result : {}", user);
			return user.getId()+":"+user.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "nouser";
		} finally {
			transport.close();
		}
	}

}
