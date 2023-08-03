package com.core.zyter.email.bulk.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.entities.BulkTemplate;
import com.core.zyter.email.bulk.vos.EmailBulkTemplateResponse;
import com.core.zyter.email.bulk.vos.EmailBulkTemplates;
import com.core.zyter.email.vos.PlaceHolder;

public interface BulkEmailTemplateService {
	
	BulkTemplate uploadTemplate(String name,MultipartFile file, String type,String subject)
			throws OmnichannelException;
	
	BulkTemplate createTemplate(String heading, String fileData, String type) throws OmnichannelException;

	EmailBulkTemplates getTemplates(Integer page, Integer size) throws OmnichannelException;

	BulkTemplate deleteTemplate(Long id) throws OmnichannelException;

	EmailBulkTemplateResponse downloadBulkTemplate(Long id, PlaceHolder placeHolder)
			throws OmnichannelException, IOException;
	
}
