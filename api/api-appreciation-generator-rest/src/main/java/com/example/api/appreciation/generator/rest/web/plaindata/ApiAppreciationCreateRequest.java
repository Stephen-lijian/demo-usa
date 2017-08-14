package com.example.api.appreciation.generator.rest.web.plaindata;

import lombok.Data;

@Data
public class ApiAppreciationCreateRequest {
	
	private Long[] helperIds;
	
	private Long[] helpeeIds;
	
	private long at;
	
	private String location;
	
	private String content;
	
	private int imgSize;
	
	private String[] imgS3BucketNames;
	
	private String[] imgS3Key;

}
