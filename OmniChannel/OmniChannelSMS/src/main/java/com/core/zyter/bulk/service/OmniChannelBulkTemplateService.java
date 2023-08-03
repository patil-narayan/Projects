/*
 * @OmniChannelBulkTemplateService.java@
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

package com.core.zyter.bulk.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.bulk.entites.BulkTemplate;
import com.core.zyter.bulk.vos.BulkTemplates;
import com.core.zyter.bulk.vos.TemplateResponse;
import com.core.zyter.exceptions.OmnichannelException;

public interface OmniChannelBulkTemplateService {
	BulkTemplate uploadTemplate(String heading, MultipartFile file, String type) throws OmnichannelException;

	BulkTemplate createTemplate(String heading, String fileData, String type) throws OmnichannelException;

	BulkTemplate deleteTemplate(Long id) throws OmnichannelException;

	TemplateResponse downloadBulkTemplate(Long id) throws OmnichannelException, FileNotFoundException, IOException;

	BulkTemplates getTemplates(Integer page, Integer size, String type) throws OmnichannelException;
}