package com.core.zyter.email.bulk.vos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkEmailRequest {
	
	String careManager;
	String note;
	Long distributionList;
	Long template;
	Map<String, String> placeholders;
	

}
