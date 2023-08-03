package com.core.zyter.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.sms.entities.SMSDeliveryStatus;
import com.core.zyter.sms.entities.SMSHistory;
import com.core.zyter.sms.service.SMSService;
import com.core.zyter.sms.vos.SMSVo;
import com.core.zyter.util.Constants;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/v1/sms")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class SMSController {
	
	@Autowired
	SMSService sendOTPService;
	
	@PostMapping("/message")
	public ResponseEntity<SMSHistory> sendMessage(
			@RequestParam("saveMessage") boolean saveMessage,
			@RequestBody SMSVo smsVo) throws OmnichannelException {

		SMSHistory sms = sendOTPService.sendMessage(smsVo);
	    return ResponseEntity.ok().body(sms);
		
	}
	
	   @RequestMapping(value = "/deliveryStatus", method = RequestMethod.POST, produces = "text/xml")
	    @ResponseBody
	    @ResponseStatus(value = HttpStatus.OK)
	    public String receiveDeliveryStatus(SMSDeliveryStatus smsOTPDeliveryStatus) {

	        log.info("smsOTPDeliveryStatus: {}", smsOTPDeliveryStatus);
	      return  sendOTPService.receiveDeliveryStatus(smsOTPDeliveryStatus);
	    }
}
