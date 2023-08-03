package com.core.zyter.email.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.dao.EmailTemplateRepository;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.service.EmailTemplateService;
import com.core.zyter.email.util.OmniChannelEmailUtil;
import com.core.zyter.email.vos.DynamicPlaceHolder;
import com.core.zyter.email.vos.PlaceHolder;
import com.core.zyter.email.vos.TemplateResponse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Data
public class EmailTemplateServiceImpl implements EmailTemplateService {

	@Autowired
	EmailTemplateRepository emailTemplateRepository;

	@Override
	public List<EmailTemplate> getTemplates() throws OmnichannelException {
			 List<EmailTemplate> emailTempalte = emailTemplateRepository.getTemplates();
			 if(emailTempalte.isEmpty()) {
				throw new OmnichannelException("Templates does not exist",null, HttpStatus.NOT_FOUND);
	}
	return emailTempalte;
	}
	
	@Override
	public TemplateResponse getTemplates(Long id,PlaceHolder placeHolder) throws OmnichannelException, IOException {
		TemplateResponse templateResponse = null;
		Optional<EmailTemplate> emailTemplate = emailTemplateRepository.findById(id);
		if (emailTemplate.isEmpty()) {
			throw new OmnichannelException(String.format("Template Id: %s does not exist", id), null,
					HttpStatus.NOT_FOUND);
		}
		Path fileName = Path.of(emailTemplate.get().getFilePath());
		String template = Files.readString(fileName);
		if(placeHolder != null) {
		DynamicPlaceHolder dynamicPlaceHolder = OmniChannelEmailUtil.placeHolderForEmail(template,emailTemplate.get().getSubject() ,placeHolder.getPlaceholders());
		templateResponse = TemplateResponse.builder().content(dynamicPlaceHolder.getTemplateContent()).emailTemplate(emailTemplate).build();
		}else {
			templateResponse = TemplateResponse.builder().content(template).emailTemplate(emailTemplate).build();
		}
		
		return templateResponse;
	}

}
