package com.example.common.exception.appreciation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AppreciationCreatorInHelpersException extends
		AppreciationException {

	private static final long serialVersionUID = -681575044337406069L;

	public AppreciationCreatorInHelpersException() {
	}
}
