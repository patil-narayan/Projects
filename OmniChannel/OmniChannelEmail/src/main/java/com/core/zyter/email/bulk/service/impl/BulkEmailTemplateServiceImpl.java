package com.core.zyter.email.bulk.service.impl;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.dao.BulkEmailTemplateRepository;
import com.core.zyter.email.bulk.entities.BulkTemplate;
import com.core.zyter.email.bulk.service.BulkEmailTemplateService;
import com.core.zyter.email.bulk.vos.EmailBulkTemplateResponse;
import com.core.zyter.email.bulk.vos.EmailBulkTemplates;
import com.core.zyter.email.util.ConfigProperties;
import com.core.zyter.email.util.Constants;
import com.core.zyter.email.util.OmniChannelEmailUtil;
import com.core.zyter.email.vos.DynamicPlaceHolder;
import com.core.zyter.email.vos.PlaceHolder;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class BulkEmailTemplateServiceImpl implements BulkEmailTemplateService {
	private final String TEMPLATE_FOLDER = "/template/";
	
	@Autowired
	BulkEmailTemplateRepository bulkEmailTemplateRepository;
	
	@Autowired
	 ConfigProperties configProperties;

	@Override
	public BulkTemplate uploadTemplate(String name,MultipartFile file, String type,String subject)
			throws OmnichannelException {
		
   	 if (!bulkEmailTemplateRepository.getBulkTemplateByNameAndType(name,type).isEmpty()) {
           throw new OmnichannelException("Template Title already exists, please enter different name",
                   Constants.FAILURE, HttpStatus.BAD_REQUEST);
       }
		 BulkTemplate bulkEmailTemplate;
		 String fileType = file.getContentType();
		 if (!fileType.matches(Constants.HTML_TXT_REGEX))
	            throw new OmnichannelException("Accepted File format is html/txt ", Constants.FAILURE, HttpStatus.BAD_REQUEST);
	        String fileName =file.getOriginalFilename();
	        if(fileName.contains(".txt")) {
	        fileName = (fileName.substring(0, fileName.lastIndexOf(".")) +
	                System.currentTimeMillis()).replace(" ", "_") + ".txt";
	        }else {
	        	fileName = (fileName.substring(0, fileName.lastIndexOf(".")) +
		                System.currentTimeMillis()).replace(" ", "_") + ".html";
	        }
	        if(Constants.MAX_FILE_SIZE_IN_MB < file.getSize() ) {
	        	throw new OmnichannelException("Maximum upload size exceeded.",
	        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
	        }
 	        Path filePath;
	        try {
	            filePath = Paths.get(configProperties.getUploadFilePath() + TEMPLATE_FOLDER + fileName).toAbsolutePath().normalize();
	            Files.createDirectories(filePath);
	            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	            bulkEmailTemplate = BulkTemplate.builder().name(name).subject(subject).filePath(filePath.toString()).type(type).recordStatus(true).build();
	            
	            bulkEmailTemplateRepository.save(bulkEmailTemplate);
	        } catch (Exception e) {
	            log.error("", e);
	            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        return bulkEmailTemplate;
	}
	
	 @Override
	    public BulkTemplate createTemplate(String name, String template, String type) throws OmnichannelException {

	        BulkTemplate bulkEmailTemplate;
	        if (!bulkEmailTemplateRepository.getBulkTemplateByNameAndType(name,type).isEmpty()) {
	            throw new OmnichannelException("Template name already exist, please enter different name",
	                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
	        }
	        if(Constants.FILE_SIZE < template.length()) {
	        	throw new OmnichannelException("Max length of template content exceeding 1060 characters.",
	        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
	        	}

	        try {
	            String fileName = (name + System.currentTimeMillis()).replace(" ", "_") + ".html";
	            Path filePath = Paths.get(configProperties.getUploadFilePath() + fileName).toAbsolutePath().normalize();
	            Files.createDirectories(filePath);
	            Files.copy(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)), filePath, StandardCopyOption.REPLACE_EXISTING);
	            bulkEmailTemplate = BulkTemplate.builder().name(name).filePath(filePath.toString()).type(Constants.EMAIL).recordStatus(true).build();
	            bulkEmailTemplateRepository.save(bulkEmailTemplate);
	        } catch (Exception e) {
	            log.error("", e);
	            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        return bulkEmailTemplate;
	    }

	    public EmailBulkTemplates getTemplates(Integer page, Integer size) throws OmnichannelException {
	        EmailBulkTemplates emailBulkTemplates;
	        List<BulkTemplate> bulkEmailTemplatelist;

	        try {
	        if (page != null && size != null) {
	            Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
	            Page<BulkTemplate> templatePage = bulkEmailTemplateRepository.getTemplate(pageable);
	            bulkEmailTemplatelist = new ArrayList<>(templatePage.getContent());
	            emailBulkTemplates = EmailBulkTemplates.builder().bulkTemplates(bulkEmailTemplatelist)
	                    .totalPages(templatePage.getTotalPages())
	                    .totalItems(templatePage.getTotalElements())
	                    .currentPage(templatePage.getNumber())
	                    .size(templatePage.getSize())
	                    .build();
	        } else {
	        	bulkEmailTemplatelist= new ArrayList<>(bulkEmailTemplateRepository.getAllTemplate());
	            Collections.reverse(bulkEmailTemplatelist);
	            emailBulkTemplates = EmailBulkTemplates.builder().bulkTemplates(bulkEmailTemplatelist)
	            		.totalItems(bulkEmailTemplateRepository.getTotalElements()).build();
	        }
	        }catch(Exception ex) {
	        	 log.error("", ex);
	             throw new OmnichannelException(ex.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        return emailBulkTemplates;
	    }

	    @Override
	    public BulkTemplate deleteTemplate(Long id) throws OmnichannelException {
	        Optional<BulkTemplate> bulkTemplate = bulkEmailTemplateRepository.findById(id);
	        if (bulkTemplate.isEmpty()) {
	            throw new OmnichannelException("Template Id: " + id + " doest exist", Constants.FAILURE, HttpStatus.NOT_FOUND);
	        }
	        try {
	        	bulkTemplate.get().setRecordStatus(false);
	        	bulkEmailTemplateRepository.save(bulkTemplate.get());
	        } catch (Exception e) {
	            log.error("", e);
	            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        return bulkTemplate.get();
	    }

	    @Override
	    public EmailBulkTemplateResponse downloadBulkTemplate(Long id,PlaceHolder placeHolder) throws OmnichannelException, java.io.IOException {
	    	EmailBulkTemplateResponse emailBulkTemplateResponse = null;
	        Optional<BulkTemplate> bulkTemplate = bulkEmailTemplateRepository.findById(id);
	        if (bulkTemplate.isEmpty()) {
	            throw new OmnichannelException(String.format("Template Id: %s does not exist", id), Constants.FAILURE,
	                    HttpStatus.NOT_FOUND);
	        }
	        Path fileName = Path.of(bulkTemplate.get().getFilePath());
	        String template = Files.readString(fileName);
	        if(placeHolder !=null) {
	        	DynamicPlaceHolder dynamicPlaceHolder = OmniChannelEmailUtil.placeHolderForEmail(template, bulkTemplate.get().getSubject(), placeHolder.getPlaceholders());
	        	emailBulkTemplateResponse = EmailBulkTemplateResponse.builder().content(dynamicPlaceHolder.getTemplateContent()).bulkTemplate(bulkTemplate).build();
	        }else {
	        	emailBulkTemplateResponse = EmailBulkTemplateResponse.builder().content(template).bulkTemplate(bulkTemplate).build();
	        }   
	        return emailBulkTemplateResponse;
	    }


	

	
}
