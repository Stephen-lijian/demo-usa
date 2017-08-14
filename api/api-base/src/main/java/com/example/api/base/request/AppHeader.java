package com.example.api.base.request;

import lombok.Data;

/**
 * 通用的APP请求头
 * @author zuozhengfeng
 *
 */
@Data
public abstract class AppHeader {
	
	private long timestamp;
	
	private String md5;

	private String mobileInfo;
	
	private String deviceId;

	private String os;
	
	private String osv;
	
	private String appv;
	
	private int appvi;
	
	private String acceptLanguage;
	
	private Long lang;
	
	private String lot; 
	
	private String loginToken;
	
	private Long userId;
	
}
