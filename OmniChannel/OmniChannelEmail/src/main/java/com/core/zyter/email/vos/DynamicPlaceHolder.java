package com.core.zyter.email.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DynamicPlaceHolder {
	
	String emailSubject;
	String templateContent;

}
