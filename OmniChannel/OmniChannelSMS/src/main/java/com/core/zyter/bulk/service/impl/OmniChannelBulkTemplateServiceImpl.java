/*
 * @OmniChannelBulkTemplateServiceImpl.java@
 * Created on 31Jan2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.bulk.service.impl;

import com.core.zyter.bulk.dao.BulkTemplateAuditRepository;
import com.core.zyter.bulk.dao.BulkTemplateRepository;
import com.core.zyter.bulk.entites.BulkTemplate;
import com.core.zyter.bulk.service.OmniChannelBulkTemplateService;
import com.core.zyter.bulk.vos.BulkTemplates;
import com.core.zyter.bulk.vos.TemplateResponse;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Data
@Slf4j
@Service
public class OmniChannelBulkTemplateServiceImpl implements OmniChannelBulkTemplateService {
    private final String TEMPLATE_FOLDER = "/template/";
    
    @Autowired
    BulkTemplateAuditRepository bulkTemplateAuditRepository;
    @Autowired
    BulkTemplateRepository bulkTemplateRepository;
    @Autowired
    ConfigProperties configProperties;

    @Override
    public BulkTemplate uploadTemplate(String name, MultipartFile file, String type) throws OmnichannelException {

    	 if (!bulkTemplateRepository.getBulkTemplateByNameAndType(name,type).isEmpty()) {
            throw new OmnichannelException("Template Title already exists, please enter different name",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }

        String fileName = file.getOriginalFilename();
        fileName = (fileName.substring(0, fileName.lastIndexOf(".")) +
                System.currentTimeMillis()).replace(" ", "_") + ".txt";
        Path filePath;
        String fileType = file.getContentType();
        BulkTemplate bulkTemplate;
        if (!fileType.equals(Constants.FILE_TYPE_TEXT))
            throw new OmnichannelException("Accepted File format is text", Constants.FAILURE, HttpStatus.BAD_REQUEST);
        if(Constants.FILE_SIZE < file.getSize()) {
        	throw new OmnichannelException("Max length of template content exceeding 1060 characters.",
        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
        	}
        if(Constants.MAX_FILE_SIZE_IN_MB < file.getSize()) {
        	throw new OmnichannelException("Maximum upload size exceeded.",
        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        try {
            filePath = Paths.get(configProperties.getUploadFilePath() + TEMPLATE_FOLDER + fileName).toAbsolutePath().normalize();
            Files.createDirectories(filePath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            /*bulkTemplateAuditRepository.save(BulkTemplateAudit.builder().
                    name(name).type(fileType).status(Constants.UPLOAD).path(path.toString()).build());*/
            bulkTemplate = BulkTemplate.builder().name(name).filePath(filePath.toString()).type(type).recordStatus(true).build();
            bulkTemplateRepository.save(bulkTemplate);
        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return bulkTemplate;
    }

    @Override
    public BulkTemplate createTemplate(String name, String template, String type) throws OmnichannelException {

        BulkTemplate bulkTemplate;
      	 if (!bulkTemplateRepository.getBulkTemplateByNameAndType(name,type).isEmpty()) {
           throw new OmnichannelException("Template Title already exists, please enter different name",
                   Constants.FAILURE, HttpStatus.BAD_REQUEST);
       }
        if(Constants.FILE_SIZE < template.length()) {
        	throw new OmnichannelException("Max length of template content exceeding 1060 characters.",
        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
        	}
        try {

            String fileName = (name + System.currentTimeMillis()).replace(" ", "_") + ".txt";
            Path filePath = Paths.get(configProperties.getUploadFilePath() + fileName).toAbsolutePath().normalize();
            Files.createDirectories(filePath);
            Files.copy(new ByteArrayInputStream(template.getBytes(StandardCharsets.UTF_8)), filePath, StandardCopyOption.REPLACE_EXISTING);
            /*bulkTemplateAuditRepository.save(BulkTemplateAudit.builder().fileName(fileName).type(Constants.FILE_TYPE_TEXT).status(status)
                    .path(filePath.toString()).build());*/
            bulkTemplate = BulkTemplate.builder().name(name).filePath(filePath.toString()).type(Constants.SMS).recordStatus(true).build();
            bulkTemplateRepository.save(bulkTemplate);
        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return bulkTemplate;
    }
    @Override
    public BulkTemplates getTemplates(Integer page, Integer size,String type) throws OmnichannelException {
        BulkTemplates bulkTemplates;
        List<BulkTemplate> templateList;

        try {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

            Page<BulkTemplate> templatePage = bulkTemplateRepository.getTemplate(pageable);
            templateList = new ArrayList<>(templatePage.getContent());
            if(StringUtils.isNotBlank(type)) {
            	Page<BulkTemplate> templateByType = bulkTemplateRepository.getTemplateAndType(pageable,type);
            	templateList = new ArrayList<>(templateByType.getContent());
            	 bulkTemplates = BulkTemplates.builder().bulkTemplates(templateList)
                         .totalPages(templateByType.getTotalPages())
                         .totalItems(templateByType.getTotalElements())
                         .currentPage(templateByType.getNumber())
                         .size(templateByType.getSize())
                         .build();
            }else {
            bulkTemplates = BulkTemplates.builder().bulkTemplates(templateList)
                    .totalPages(templatePage.getTotalPages())
                    .totalItems(templatePage.getTotalElements())
                    .currentPage(templatePage.getNumber())
                    .size(templatePage.getSize())
                    .build();
            }
        } else {
            templateList= new ArrayList<>(bulkTemplateRepository.getAllTemplate());
            Collections.reverse(templateList);
            bulkTemplates = BulkTemplates.builder().bulkTemplates(templateList)
            		.totalItems(bulkTemplateRepository.count()).build();
        }
        }catch(Exception ex) {
        	 log.error("", ex);
             throw new OmnichannelException(ex.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return bulkTemplates;
    }

    @Override
    public BulkTemplate deleteTemplate(Long id) throws OmnichannelException {
        Optional<BulkTemplate> bulkTemplate = bulkTemplateRepository.findById(id);
        if (bulkTemplate.isEmpty()) {
            throw new OmnichannelException("Template Id: " + id + " doest exist", Constants.FAILURE, HttpStatus.NOT_FOUND);
        }
        try {
        	bulkTemplate.get().setRecordStatus(false);
         	bulkTemplateRepository.save(bulkTemplate.get());
        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return bulkTemplate.get();
    }

    @Override
    public TemplateResponse downloadBulkTemplate(Long id) throws OmnichannelException, java.io.IOException {
        TemplateResponse templateResponse = null;
        Optional<BulkTemplate> bulkTemplate = bulkTemplateRepository.findById(id);
        if (bulkTemplate.isEmpty()) {
            throw new OmnichannelException(String.format("Template Id: %s does not exist", id), Constants.FAILURE,
                    HttpStatus.NOT_FOUND);
        }
        Path fileName = Path.of(bulkTemplate.get().getFilePath());
        String template = Files.readString(fileName);
        templateResponse = TemplateResponse.builder().content(template).bulkTemplate(bulkTemplate).build();
        return templateResponse;
    }

}