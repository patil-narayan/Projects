package com.core.zyter.email.vos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequest {
	
	String careManager;
	String member;
	String templateId;
	Map<String, String> placeholders;


}
