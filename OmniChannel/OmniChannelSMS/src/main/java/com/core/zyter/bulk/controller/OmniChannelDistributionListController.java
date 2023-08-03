/*
 * @OmniChannelBulkSMSController.java@
 * Created on 16Jan2023
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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.bulk.entites.DistributionList;
import com.core.zyter.bulk.service.OmniChannelBulkDistributionListService;
import com.core.zyter.bulk.vos.BulkMessageResponse;
import com.core.zyter.bulk.vos.DistributionListResponse;
import com.core.zyter.bulk.vos.DistributionLists;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/api/v1/bulk/")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelDistributionListController {

    @Autowired
    OmniChannelBulkDistributionListService omniChannelBulkDistributionListService;

    @RequestMapping(value = "/distributionList", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<DistributionList> uploadDistributionList(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) throws IOException, OmnichannelException {

        return ResponseEntity.ok().body(omniChannelBulkDistributionListService.uploadDistributionList(name, file,type));

    }

    @RequestMapping(value = "/distributionList", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<DistributionLists> getDistributionList(@RequestParam(required = false, name = "page") Integer page,
                                                                 @RequestParam(required = false, name = "size") Integer size,
                                                                 @RequestParam(required = false, name = "type") String type) throws OmnichannelException {

        return ResponseEntity.ok().body(omniChannelBulkDistributionListService.getDistributionList(page, size,type));
    }

    @RequestMapping(value = "/distributionList/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<DistributionListResponse>> downloadDistributionList(@PathVariable("id") Long id) throws OmnichannelException, CsvValidationException, IOException, CsvException {


    	List<DistributionListResponse> resource = omniChannelBulkDistributionListService.downloadDistributionList(id);

        return ResponseEntity.ok().body(resource);

    }

    @RequestMapping(value = "/distributionList/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<BulkMessageResponse> deleteDistributionList(@PathVariable("id") Long id) throws OmnichannelException {


        DistributionList distributionList = omniChannelBulkDistributionListService.deleteDistributionList(id);

        return ResponseEntity.ok().body(BulkMessageResponse.builder()
                .status(distributionList.getName()+ ": DistributionList deleted Successfully").build());

    }


}
