package com.core.zyter.bulk.vos;

import java.util.Optional;

import com.core.zyter.entites.TemplateType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateTypeResponse {

	Optional<TemplateType> templateType;
	String content;
	
}
