package com.core.zyter.sms.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SMSVo {
	
	private String from;
	private String to;
	private String toPhoneNumber;
	private String msg;
	String type;
	

}
