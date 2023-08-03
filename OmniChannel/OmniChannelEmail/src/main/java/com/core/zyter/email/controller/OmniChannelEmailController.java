/*
 * @OmniChannelEmailController.java@
 * Created on 10Mar2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.email.controller;


import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.entities.EmailEventDetails;
import com.core.zyter.email.entities.EmailHistory;
import com.core.zyter.email.service.EmailService;
import com.core.zyter.email.vos.EmailHistoryVos;
import com.core.zyter.email.vos.EmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/email")
@SecurityRequirement(name = "Bearer Authentication")
public class OmniChannelEmailController {

    @Autowired
    EmailService emailService;

    @Operation(summary = "Welcome API", description = "Welcome API")
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        String body = "<h1>Hello! Welcome to OmniChannel EMAIL !!</h1> ";
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }


    @GetMapping("/emailHistory")
    public ResponseEntity<EmailHistoryVos> history(@RequestParam String userId, @RequestParam String memberId,
                                                   @RequestParam int page, @RequestParam int size,@RequestParam(required = false,name="clientOffset") Long clientOffset)
            throws MessagingException, OmnichannelException {

        EmailHistoryVos email = emailService.getEmailHistory(userId, memberId, page, size,clientOffset);
        return ResponseEntity.status(HttpStatus.OK).body(email);
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<EmailHistory> sendEmail(@RequestBody EmailRequest emailRequest,@RequestParam(required = false)Long clientOffset)
            throws OmnichannelException, IOException {
        return ResponseEntity.status(HttpStatus.OK).body(emailService.sendEmail(emailRequest,clientOffset));
    }
    
    @PostMapping(value = "/emailEvent")
   	@ResponseBody
   	@ResponseStatus(value = HttpStatus.OK)
   	public ResponseEntity<String> emailEvent(@RequestBody List<EmailEventDetails> payload)
   			throws OmnichannelException {
   		return ResponseEntity.ok().body(emailService.deliveryEvent(payload));
   	} 

}
