package com.core.zyter.email.bulk.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailBulkMessageResponse {

	EmailBulkMessage bulkMessage;
	String status;

}
