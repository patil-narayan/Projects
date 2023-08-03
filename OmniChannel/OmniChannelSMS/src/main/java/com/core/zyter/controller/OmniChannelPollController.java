/*
 * @OmniChannelPollController.java@
 * Created on 02Feb2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.bulk.vos.TemplateTypeResponse;
import com.core.zyter.bulk.vos.UnreadMessageCount;
import com.core.zyter.entites.DeliveryStatus;
import com.core.zyter.entites.SmsConversation;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.OmniChannelPollService;
import com.core.zyter.util.Constants;
import com.core.zyter.vos.MappingRequest;
import com.core.zyter.vos.MappingResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelPollController {

    @Autowired
    OmniChannelPollService omniChannelPollService;


    @GetMapping("/message")
    public List<SmsConversation> getMessages(@RequestParam String date, @RequestParam String caremanager,
                                             @RequestParam String member,
                                             @RequestParam(required = false, name = "clientOffset") Long clientOffset) throws ParseException, OmnichannelException {

        return omniChannelPollService.getReceiveMessages(date, caremanager, member, clientOffset);

    }

    @PostMapping("/delivery_status")
    public List<DeliveryStatus> getDeliveryStatus(@RequestBody List<String> smsId) throws OmnichannelException {

        return omniChannelPollService.getDeliveryStatus(smsId);

    }

    @GetMapping("/unreadMessage")
    public UnreadMessageCount UnReadMessages(@RequestParam String caremanager, @RequestParam String member)
            throws ParseException, OmnichannelException {
        return omniChannelPollService.getUnreadMessageCount(caremanager, member);
    }
    
    @GetMapping("/templateFormat")
    public ResponseEntity<TemplateTypeResponse> templateFormat(@RequestParam("name") String name,
    		@RequestParam("type") String type,@RequestParam("mode") String mode)
    				throws OmnichannelException, IOException {
    	TemplateTypeResponse resource = omniChannelPollService.templateFormat(name,type,mode);
    	return ResponseEntity.ok().body(resource);
    }
    @PostMapping("/mapping")
    public ResponseEntity<MappingResponse> validatePhoneNumberAndEmailId(@RequestBody MappingRequest mappingRequest) throws OmnichannelException{
    	MappingResponse mappingResponse=omniChannelPollService.phoneNumberAndEMailIdValidation(mappingRequest);
    	return ResponseEntity.ok().body(mappingResponse);		
    }
}
