package com.core.zyter.bulk.vos;

import java.util.Optional;

import com.core.zyter.bulk.entites.BulkTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {
	Optional<BulkTemplate> bulkTemplate;
	String content;
}
