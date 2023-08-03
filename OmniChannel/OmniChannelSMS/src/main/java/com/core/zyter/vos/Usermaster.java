package com.core.zyter.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usermaster {
	
	String userId;
	String firstName;
	String middleName;
	String lastName;
	String userType;
	String phoneNumber;
	String emailId;
	boolean active;

}
