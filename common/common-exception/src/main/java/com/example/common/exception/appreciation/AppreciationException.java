package com.example.common.exception.appreciation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.example.common.exception.MsException;

@Data
@EqualsAndHashCode(callSuper=false)
public class AppreciationException  extends MsException {

	private static final long serialVersionUID = -2482786977831433838L;

	public AppreciationException() {
	}
}
