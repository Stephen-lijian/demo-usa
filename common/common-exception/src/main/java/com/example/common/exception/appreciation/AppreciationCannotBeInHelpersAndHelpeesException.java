package com.example.common.exception.appreciation;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class AppreciationCannotBeInHelpersAndHelpeesException extends
	AppreciationException {

	private static final long serialVersionUID = 8065444633552840410L;

	public AppreciationCannotBeInHelpersAndHelpeesException() {
	}
	
}

