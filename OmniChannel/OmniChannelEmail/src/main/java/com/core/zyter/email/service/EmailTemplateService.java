package com.core.zyter.email.service;

import java.io.IOException;
import java.util.List;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.vos.PlaceHolder;
import com.core.zyter.email.vos.TemplateResponse;


public interface EmailTemplateService {

	List<EmailTemplate> getTemplates() throws OmnichannelException;

	TemplateResponse getTemplates(Long id, PlaceHolder placeHolder) throws OmnichannelException, IOException;

}
