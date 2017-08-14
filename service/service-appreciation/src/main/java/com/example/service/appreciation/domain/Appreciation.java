package com.example.service.appreciation.domain;

import lombok.Data;

@Data
public class Appreciation {
	
	private Long id;
	
	private int source = AppreciationSource.SELF.getValue();

	private Long[] helperIds;

	private Long[] finalHelperIds = new Long[0];
	
	private Long[] helpeeIds;

	private Long[] finalHelpeeIds = new Long[0];

	private Long createBy;
	
	private Long createAt;
	
	private Long occurAt;
	
	private String location;
	
	private String content;
	
	private int imgSize;
	
	private String[] imgS3BucketNames;
	
	private String[] imgS3Keys;
	
	/** 0-初始，1-成功，2-失败 */
	private AppreciationConfirmStatus confirmStatus = AppreciationConfirmStatus.INIT;
	
	public boolean isFail() {
		return this.confirmStatus == AppreciationConfirmStatus.FAIL;
	}
	
	/* 0-无效，1-有效 */
	private Integer status = 1;
	
}
