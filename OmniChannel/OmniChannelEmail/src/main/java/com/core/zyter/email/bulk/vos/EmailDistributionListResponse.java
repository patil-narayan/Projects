package com.core.zyter.email.bulk.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDistributionListResponse {

	String firstName;
	String lastName;
	String dob;
	String Email;
}
