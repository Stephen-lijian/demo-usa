package com.example.common.plaindata.appreciation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsAppreciationCreateRequest {
	
	private Long id;
	
	private long at;
	
	private String location;
	
	private Long[] helperIds;
	
	private Long[] helpeeIds;
	
	private String content;
	
	private int imgSize;
	
	private String[] imgS3BucketNames;
	
	private String[] imgS3Key;
	
	private Long createBy;
	
	private Long createAt;

}
