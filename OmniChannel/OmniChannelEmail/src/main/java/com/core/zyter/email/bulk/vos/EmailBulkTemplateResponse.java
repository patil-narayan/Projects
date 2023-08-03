package com.core.zyter.email.bulk.vos;

import java.util.Optional;

import com.core.zyter.email.bulk.entities.BulkTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailBulkTemplateResponse {
	Optional<BulkTemplate> bulkTemplate;
	String content;
}
