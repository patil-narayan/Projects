/*
 * @OmniChannelBulkTemplateController.java@
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

package com.core.zyter.bulk.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.bulk.entites.BulkTemplate;
import com.core.zyter.bulk.service.OmniChannelBulkTemplateService;
import com.core.zyter.bulk.vos.BulkMessageResponse;
import com.core.zyter.bulk.vos.BulkTemplates;
import com.core.zyter.bulk.vos.TemplateResponse;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/bulk/")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelBulkTemplateController {

    @Autowired
    OmniChannelBulkTemplateService omniChannelBulkTemplateService;

    @RequestMapping(value = "/template", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BulkTemplate> uploadTemplate(@RequestParam("name") String name,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam("type") String type) throws OmnichannelException {
        return ResponseEntity.status(HttpStatus.OK).body(omniChannelBulkTemplateService.uploadTemplate(name, file,type));
    }

    @RequestMapping(value = "/template/{name}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BulkTemplate> createTemplate(@PathVariable("name") String name,
                                                       @RequestParam("template") String template,
                                                       @RequestParam("type") String type) throws OmnichannelException {
        return ResponseEntity.status(HttpStatus.OK).body(omniChannelBulkTemplateService.createTemplate(name, template,type));
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BulkTemplates> bulkTemplates(@RequestParam(required = false, name = "page") Integer page,
                                                       @RequestParam(required = false, name = "size") Integer size,
                                                       @RequestParam(required = false, name = "type") String type) throws OmnichannelException {
        return ResponseEntity.status(HttpStatus.OK).body(omniChannelBulkTemplateService.getTemplates(page, size,type));
    }

    @RequestMapping(value = "/template/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BulkMessageResponse> deleteTemplate(@PathVariable("id") Long id) throws OmnichannelException {
        BulkTemplate bulkTemplate = omniChannelBulkTemplateService.deleteTemplate(id);
        return ResponseEntity.ok().body(BulkMessageResponse.builder()
                .status(bulkTemplate.getName() + ": Template deleted Successfully").build());
    }

    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<TemplateResponse> downloadDistributionList(@PathVariable("id") Long id)
            throws OmnichannelException, IOException {

    	return ResponseEntity.ok().body(omniChannelBulkTemplateService.downloadBulkTemplate(id));

    }

}