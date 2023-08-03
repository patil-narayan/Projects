/*
 * @OmniChannelSMSController.java@
 * Created on 08Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.controller;

import com.core.zyter.entites.DeliveryStatus;

import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.MemberChannelOptinStatus;
import com.core.zyter.entites.OmnichannelSMS;
import com.core.zyter.entites.ReceiveSMS;
import com.core.zyter.entites.SmsConversation;
import com.core.zyter.entites.Template;
import com.core.zyter.entites.User;
import com.core.zyter.entites.UserMaster;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.OmniChannelSMSService;
import com.core.zyter.service.TwilioSMSOperationService;
import com.core.zyter.util.Constants;
import com.core.zyter.vos.MemberCareManagerRequest;
import com.core.zyter.vos.MemberPreference;
import com.core.zyter.vos.SmsConversations;
import com.core.zyter.vos.Usermaster;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Tag(name = "Single SMS", description = "API's related to Single SMS")
@Slf4j
@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelSMSController {

	@Autowired
	OmniChannelSMSService omniChannelSMSService;

	@Autowired
	TwilioSMSOperationService twilioSMSOperationService;

	@Operation(summary = "Welcome API", description = "Welcome API")
	@GetMapping("/")
	public ResponseEntity<String> hello() {
		String body = "<h1>Hello! Welcome to OmniChannel SMS !!</h1> ";
		return ResponseEntity.status(HttpStatus.OK).body(body);
	}

	@GetMapping("/users")
	@ResponseBody
	public ResponseEntity<List<UserMaster>> getUsers() {

		var users = omniChannelSMSService.getAllUsers();
		var responseEntity = ResponseEntity.status(HttpStatus.OK).body(users);
		return responseEntity;

	}

	@PostMapping("/bulkMessage")
	public ResponseEntity<String> sendBulkMessage(@RequestParam("message") String messageBody,
			@RequestParam("saveMessage") boolean saveMessage, @RequestBody List<User> users) {

		twilioSMSOperationService.notifySMS(messageBody, saveMessage, users);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Operation(summary = "Used to send SMS to particular Phone number")
	@PostMapping("/message")
	public ResponseEntity<SmsConversation> sendMessage(
			@Parameter(description = "to save message on server select True/False") @RequestParam("saveMessage") boolean saveMessage,
			@RequestBody OmnichannelSMS omnichannelSMS,
			@RequestParam(required = false, name = "clientOffset") Long clientOffset) throws OmnichannelException {

		SmsConversation smsConversation = omniChannelSMSService.sendMessage(omnichannelSMS, clientOffset);
		return ResponseEntity.ok().body(smsConversation);

	}

	@Operation(summary = "Used to send Predefined templates")
	@PostMapping("/template")
	public ResponseEntity<SmsConversation> sendSubscription(
			@Parameter(description = "Template type") @RequestParam("type") String type,
			@RequestBody OmnichannelSMS omnichannelSMS,
			@RequestParam(required = false, name = "clientOffset") Long clientOffset) throws OmnichannelException {

		SmsConversation smsConversation = omniChannelSMSService.sendTemplate(omnichannelSMS, type, clientOffset);
		return ResponseEntity.ok().body(smsConversation);

	}

	@RequestMapping(value = "/deliveryStatus", method = RequestMethod.POST, produces = "text/xml")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public String receiveDeliveryStatus(DeliveryStatus smsDeliveryStatus) {

		return omniChannelSMSService.receiveDeliveryStatus(smsDeliveryStatus);
	}

	@RequestMapping(value = "/receiveMessage", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void receiveMessage(ReceiveSMS twilioReceiveSMS,
			@RequestHeader(required = false, value = "X-Twilio-Signature") String twilioSignature)
			throws OmnichannelException {

		log.info("ReceiveMessage: {}", twilioReceiveSMS);
		log.info("TwilioSignature: {}", twilioSignature);
		omniChannelSMSService.receiveMessage(twilioReceiveSMS, twilioSignature);
	}

	@Operation(summary = " Used To Fetch SMS conversation history between sender and receiver based on page number and page size")
	@RequestMapping(value = "/smsConversation", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<SmsConversations> getSMSConversation(
			@Parameter(description = "provide sender name") @RequestParam("careManager") String careManager,
			@Parameter(description = "provide receiver name") @RequestParam("member") String member,
			@Parameter(description = "provide page number which needs to be fetched") @RequestParam("page") int page,
			@Parameter(description = "provide size") @RequestParam("size") int size,
			@RequestParam(required = false, name = "clientOffset") Long clientOffset) throws OmnichannelException {

		return ResponseEntity.status(HttpStatus.OK)
				.body(omniChannelSMSService.getSMSConversation(careManager, member, page, size, clientOffset));
	}

	@RequestMapping(value = "/numbers", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public List<String> fetchNumbers(@RequestParam("messageSid") String messageSid) {

		return twilioSMSOperationService.fetchTwilioPhonesNumbers(messageSid);
	}

	@RequestMapping(value = "/templates", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<List<Template>> getTemplates() {
		List<Template> templates = omniChannelSMSService.getTemplates();
		return ResponseEntity.status(HttpStatus.OK).body(templates);
	}

	@RequestMapping(value = "/memberPreference", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<List<MemberPreference>> getMemberPreference(@RequestParam String member)
			throws OmnichannelException {
		List<MemberPreference> memberPreference = omniChannelSMSService.getMemberPreference(member);
		return ResponseEntity.status(HttpStatus.OK).body(memberPreference);
	}

	@RequestMapping(value = "/userdetails", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<UserMaster> updateUser(@RequestBody UserMaster userMaster) throws OmnichannelException {
		UserMaster userDetails = omniChannelSMSService.createUser(userMaster);
		return ResponseEntity.ok(userDetails);
	}

	@RequestMapping(value = "/createMemberCareManagerMapping", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<MemberCareManagerMapping> createMemberCareManagerMapping(
			@RequestBody MemberCareManagerRequest request) throws OmnichannelException {

		MemberCareManagerMapping mapping = omniChannelSMSService.createMemberCareManagerMapping(request);
		return ResponseEntity.ok(mapping);
	}

	@RequestMapping(value = "/usermaster", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<UserMaster> updateUserMaster(@RequestBody Usermaster usermaster) throws OmnichannelException {
		UserMaster user = omniChannelSMSService.updateUser(usermaster);
		log.error("usermaster:{}", usermaster);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void handleWebhook(@RequestParam HashMap<String, String> payload) throws Exception {
		log.debug("Received webhook payload: {}", payload);

		twilioSMSOperationService.save(payload);
	}
	
	@RequestMapping(value = "/preference/{member}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<MemberChannelOptinStatus> updatePreference(@PathVariable("member") String member,
			@RequestBody MemberPreference memberPreference) throws OmnichannelException {
		MemberChannelOptinStatus channelOptinStatus = omniChannelSMSService.updatePreference(memberPreference, member);
		return ResponseEntity.status(HttpStatus.OK).body(channelOptinStatus);
	}

}
