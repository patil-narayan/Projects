package com.core.zyter.email.vos;

import java.util.Optional;

import com.core.zyter.email.entities.EmailTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateResponse {

	Optional<EmailTemplate> emailTemplate;
	String content;

}
