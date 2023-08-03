/*
 * @TwilioSMSOperationServiceImpl.java@
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

package com.core.zyter.service.impl;

import com.core.zyter.dao.WebhookRepository;
import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.User;
import com.core.zyter.entites.WebhookPayload;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.TwilioSMSOperationService;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.OmniChannelSMSUtil;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.messaging.v1.service.PhoneNumber;
import com.twilio.rest.notify.v1.service.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.core.zyter.util.Constants;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TwilioSMSOperationServiceImpl implements TwilioSMSOperationService {

	private final String DELIVERY_STATUS_ENDPOINT = "/api/v1/deliveryStatus";
	private final String SMS_DELIVERY_STATUS_ENDPOINT = "/api/v1/sms/deliveryStatus";
	@Autowired
	ConfigProperties configProperties;

	@Autowired
	private WebhookRepository webhookRepository;

	@PostConstruct
	public void initTwilioAccount() {
		Twilio.init(configProperties.getAccountSid(), configProperties.getAccountToken());
	}

	@Override
	public Notification notifySMS(String messageBody, boolean saveMessage, List<User> users) {
		List<String> toBindings = OmniChannelSMSUtil.createBinding(users);

		Notification notification = Notification.creator(configProperties.getNotifySid()).setBody(messageBody)
				.setToBinding(toBindings).setTitle("TESTING SMS")
				.setDeliveryCallbackUrl(configProperties.getCallbackUrl() + DELIVERY_STATUS_ENDPOINT).create();
		return notification;
	}

	@Override
	public Message sendMessage(MemberCareManagerMapping memberCareManager, String messageStr) {

		Message message = Message
				.creator(new com.twilio.type.PhoneNumber(memberCareManager.getMemberPhoneNumber()),
						new com.twilio.type.PhoneNumber(memberCareManager.getCareManagerPhoneNumber()), messageStr)
				.setStatusCallback(URI.create(configProperties.getCallbackUrl() + DELIVERY_STATUS_ENDPOINT)).create();
		// Message.deleter(message.getSid()).delete();
		return message;

	}

	public Message sendMessage(String fromNumber, String toNumber, String msg) {

		log.error("Delivery Status {} :" + configProperties.getCallbackUrl() + SMS_DELIVERY_STATUS_ENDPOINT);
		Message message = Message
				.creator(new com.twilio.type.PhoneNumber(toNumber), new com.twilio.type.PhoneNumber(fromNumber), msg)
				.setStatusCallback(URI.create(configProperties.getCallbackUrl() + SMS_DELIVERY_STATUS_ENDPOINT))
				.create();
		return message;

	}

	/**
	 * @return
	 */
	@Override
	public List<String> fetchTwilioPhonesNumbers(String messageSid) {

		ResourceSet<PhoneNumber> phoneNumbers = PhoneNumber.reader(messageSid).read();

		List<String> phoneNumberList = new ArrayList<>();
		for (PhoneNumber phoneNumber : phoneNumbers) {
			phoneNumberList.add(phoneNumber.getPhoneNumber().getEndpoint());
		}
		log.info("phoneNumberList:: {}", phoneNumberList);

		return phoneNumberList;
	}

	@Override
	public void deleteMessage(String messageSid) throws OmnichannelException {

		try {

			Message.deleter(messageSid).delete();

		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException("messageSid is not exist", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@Override
	public void save(Map<String, String> payload) throws Exception {
		WebhookPayload webhookPayload = new WebhookPayload();
		webhookPayload.setPayload(payload.get("Payload"));
		webhookPayload.setLevel(payload.get("Level"));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date timestamp = dateFormat.parse(payload.get("Timestamp"));
		webhookPayload.setTimestamp(timestamp);

		webhookPayload.setPayloadType(payload.get("PayloadType"));
		webhookPayload.setParentAccountSid(payload.get("ParentAccountSid"));
		webhookPayload.setAccountSid(payload.get("AccountSid"));
		webhookPayload.setSid(payload.get("Sid"));

		webhookRepository.save(webhookPayload);
	}

}
