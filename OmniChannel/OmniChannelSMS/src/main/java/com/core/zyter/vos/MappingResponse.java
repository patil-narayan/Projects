package com.core.zyter.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MappingResponse {

	boolean memberPhoneNumberExist;
	boolean memberEmailIdExist;
	boolean careManagerPhoneNumberExist;
	boolean careManagerEmailIdExist;
	
}
