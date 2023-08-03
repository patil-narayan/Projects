package com.core.zyter.bulk.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkMessage {

	String careManager;
	String note;
	Long distributionList;
	Long template;

}
