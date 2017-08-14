package com.example.service.appreciation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AppreciationVo extends Appreciation {
	
	private boolean allHelpeeConfirm = false;
	
	private boolean allHelperConfirm = false;
	

}
