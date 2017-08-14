package com.example.api.file.rest.web.plaindata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiFileUploadResp {
	
	private String message = "success";
	
	private String imgkey;
	
	public ApiFileUploadResp(String imgkey) {
		this.imgkey = imgkey;
	}

}
