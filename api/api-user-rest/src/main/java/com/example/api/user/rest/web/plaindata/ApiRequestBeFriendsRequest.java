package com.example.api.user.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiRequestBeFriendsRequest {
	
	private Long userId;
	
	private Long[] userIds;

}
