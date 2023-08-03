/*
 * @OmniChannelSMSServiceImpl.java@
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.dao.DeliveryStatusRepository;
import com.core.zyter.dao.MemberCareManagerRepository;
import com.core.zyter.dao.MemberChannelOptinRepository;
import com.core.zyter.dao.ReceiveSMSRepository;
import com.core.zyter.dao.SMSConversationRepository;
import com.core.zyter.dao.TemplateRepository;
import com.core.zyter.dao.UserMasterRepository;
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
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import com.core.zyter.util.DateTimeUtil;
import com.core.zyter.vos.MemberCareManagerRequest;
import com.core.zyter.vos.MemberPreference;
import com.core.zyter.vos.SmsConversations;
import com.core.zyter.vos.Usermaster;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.security.RequestValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OmniChannelSMSServiceImpl implements OmniChannelSMSService {

	private final String RECEIVE_MESSAGE = "/api/v1/receiveMessage";

	@Autowired
	DeliveryStatusRepository deliveryStatusRepository;
	@Autowired
	ReceiveSMSRepository receiveSMSRepository;
	@Autowired
	SMSConversationRepository smsConversationRepository;
	@Autowired
	TwilioSMSOperationService twilioSMSOperationService;
	@Autowired
	UserMasterRepository userMasterRepository;
	@Autowired
	MemberCareManagerRepository memberCareManagerRepository;
	@Autowired
	MemberChannelOptinRepository memberChannelOptinRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	ConfigProperties configProperties;

	@Value("${sms.accountToken}")
	private String authToken;

	@Value("${sms.callbackUrl}")
	private String twilioWebhookUrl;

	@Override
	public List<UserMaster> getAllUsers() {
		return userMasterRepository.findAll();
	}

	@Override
	public int addMessage(String notifySid, List<User> users) {
		return 0;
	}

	@Override
	public String receiveDeliveryStatus(DeliveryStatus twilioSMSDeliveryStatus) {
		try {
			deliveryStatusRepository.save(twilioSMSDeliveryStatus);

			if (twilioSMSDeliveryStatus.getMessageStatus().equalsIgnoreCase(Constants.DELIVERED)) {

				twilioSMSOperationService.deleteMessage(twilioSMSDeliveryStatus.getSmsSid());

				System.out.println("Delivered Message is deleted");
			}
		} catch (Exception e) {
			log.error("", e);
			return Constants.FAILURE;
		}
		return Constants.SUCCESS;
	}

	@Override
	public SmsConversation receiveMessage(ReceiveSMS twilioReceiveSMS, String twilioSignature)
			throws OmnichannelException {

		Map<String, String> params = twilioReceiveSMSParams(twilioReceiveSMS);
		// Initializing the validator
		RequestValidator validator = new RequestValidator(authToken);
		String webhookUrl = twilioWebhookUrl + RECEIVE_MESSAGE;
		boolean isValidateRequest = validator.validate(webhookUrl, params, twilioSignature);

		if (!isValidateRequest) {
			log.error("Twilio Signature validation:" + isValidateRequest);
			throw new OmnichannelException("Twilio Signature validation request fail ======", Constants.FAILURE,
					HttpStatus.FORBIDDEN);
		}
		SmsConversation smsConversation;
		try {
			String msgBody = twilioReceiveSMS.getBody().trim();
			MemberCareManagerMapping memberCareManager = memberCareManagerRepository
					.getByMemberPhoneNumberAndCareManagerPhoneNumber(twilioReceiveSMS.getFrom(),
							twilioReceiveSMS.getTo());
			smsConversation = SmsConversation.builder().userId(memberCareManager.getCareManager())
					.senderFullName(memberCareManager.getCareManagerFullName()).direction(Constants.INBOUND)
					.memberId(memberCareManager.getMember()).receiverFullName(memberCareManager.getMemberFullName())
					.fromNumber(twilioReceiveSMS.getFrom()).toNumber(twilioReceiveSMS.getTo())
					.messageType(Constants.RECEIVE).accountSid(twilioReceiveSMS.getAccountSid())
					.sid(twilioReceiveSMS.getSmsSid()).numSegments(twilioReceiveSMS.getNumSegments())
					.numMedia(twilioReceiveSMS.getNumMedia()).body(twilioReceiveSMS.getBody()).messageStatus(1)
					.createdOn(new Date()).build();
			smsConversationRepository.save(smsConversation);
			receiveSMSRepository.save(twilioReceiveSMS);

			if (Constants.START.equalsIgnoreCase(msgBody) || Constants.STOP.equalsIgnoreCase(msgBody)) {
				MemberChannelOptinStatus memberChannelOptinStatus = MemberChannelOptinStatus.builder()
						.member(memberCareManager.getMember())
						.optinStatus(
								msgBody.equalsIgnoreCase(Constants.START) ? Constants.ACCEPTED : Constants.DECLINED)
						.channelType(Constants.SMS).build();
				memberChannelOptinRepository.save(memberChannelOptinStatus);
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return smsConversation;
	}

	private Map<String, String> twilioReceiveSMSParams(ReceiveSMS twilioReceiveSMS) {
		Map<String, String> params = new LinkedHashMap<>();
		params.put("AccountSid", twilioReceiveSMS.getAccountSid());
		params.put("ApiVersion", twilioReceiveSMS.getApiVersion());
		params.put("Body", twilioReceiveSMS.getBody());
		params.put("From", twilioReceiveSMS.getFrom());
		params.put("FromCity", twilioReceiveSMS.getFromCity());
		params.put("FromCountry", twilioReceiveSMS.getFromCountry());
		params.put("FromState", twilioReceiveSMS.getFromState());
		params.put("FromZip", twilioReceiveSMS.getFromZip());
		params.put("MessageSid", twilioReceiveSMS.getMessageSid());
		params.put("MessagingServiceSid", twilioReceiveSMS.getMessagingServiceSid());
		params.put("NumMedia", twilioReceiveSMS.getNumMedia());
		params.put("NumSegments", twilioReceiveSMS.getNumSegments());
		// params.put("ReferralNumMedia",
		// twilioReceiveSMS.getReferralNumMedia());,Twilio has removed it from SMS
		// response.
		params.put("SmsMessageSid", twilioReceiveSMS.getSmsMessageSid());
		params.put("SmsSid", twilioReceiveSMS.getSmsSid());
		params.put("SmsStatus", twilioReceiveSMS.getSmsStatus());
		params.put("To", twilioReceiveSMS.getTo());
		params.put("ToCity", twilioReceiveSMS.getToCity());
		params.put("ToCountry", twilioReceiveSMS.getToCountry());
		params.put("ToState", twilioReceiveSMS.getToState());
		params.put("ToZip", twilioReceiveSMS.getToZip());
		return params;
	}

	@Override
	public SmsConversation sendMessage(OmnichannelSMS omnichannelSMS, Long clientOffset) throws OmnichannelException {
		UserMaster careManager = getUserDetails(omnichannelSMS.getFrom());
		UserMaster member = getUserDetails(omnichannelSMS.getTo());
		SmsConversation smsConversation = new SmsConversation();
		if (omnichannelSMS.getMessage().length() > 1060) {
			throw new OmnichannelException("Max length of message exceeding 1060 characters.", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}

		try {
			MemberCareManagerMapping memberCareManager = getMemberCareManagerMapping(omnichannelSMS.getTo(),
					omnichannelSMS.getFrom());
			Message message = twilioSMSOperationService.sendMessage(memberCareManager, omnichannelSMS.getMessage());
			smsConversation = createSMSConversation(member, careManager, message);
			log.info("smsConversations: {}", smsConversation);
			smsConversationRepository.save(smsConversation);
			try {
				if (null != clientOffset) {
					long serverOffset = DateTimeUtil.getServerOffset(new Date());
					smsConversation.setCreatedOn(
							DateTimeUtil.calculateDate(serverOffset, clientOffset, smsConversation.getCreatedOn()));
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return smsConversation;
	}

	public SmsConversation sendTemplate(OmnichannelSMS omnichannelSMS, String type, Long clientOffset)
			throws OmnichannelException {
		UserMaster careManager = getUserDetails(omnichannelSMS.getFrom());
		UserMaster member = getUserDetails(omnichannelSMS.getTo());
		String status = memberChannelOptinRepository.getMemberStatus(member.getUserId());
		SmsConversation smsConversation;
		if (Constants.DECLINED.equalsIgnoreCase(status)) {
			throw new OmnichannelException("Attempt to send to unsubscribed recipient", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (omnichannelSMS.getMessage().length() > 1060) {
			throw new OmnichannelException("Max length of template content exceeding 1060 characters.",
					Constants.FAILURE, HttpStatus.BAD_REQUEST);
		}
		try {
			MemberCareManagerMapping memberCareManager = getMemberCareManagerMapping(omnichannelSMS.getTo(),
					omnichannelSMS.getFrom());
			if (Constants.OPT_IN_OUT.equalsIgnoreCase(type)
					&& (null == status || Constants.SENT.equalsIgnoreCase(status))) {
				MemberChannelOptinStatus memberChannelOptinStatus = MemberChannelOptinStatus.builder()
						.member(memberCareManager.getMember()).channelType(Constants.SMS).optinStatus(Constants.SENT)
						.build();
				memberChannelOptinRepository.save(memberChannelOptinStatus);
			}
			Message message = twilioSMSOperationService.sendMessage(memberCareManager, omnichannelSMS.getMessage());
			smsConversation = createSMSConversation(member, careManager, message);
			log.info("smsConversations: {}", smsConversation);
			smsConversationRepository.save(smsConversation);
			try {
				if (null != clientOffset) {
					long serverOffset = DateTimeUtil.getServerOffset(new Date());
					smsConversation.setCreatedOn(
							DateTimeUtil.calculateDate(serverOffset, clientOffset, smsConversation.getCreatedOn()));
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}

		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return smsConversation;
	}

	@Override
	public void fetchTwilioPhonesNumbers() {
	}

	@Override
	public SmsConversations getSMSConversation(String careManager, String member, int page, int size, Long clientOffset)
			throws OmnichannelException {
		SmsConversations smsConversations;
		if (StringUtils.isBlank(careManager) || StringUtils.isBlank(member)) {
			throw new OmnichannelException("Caremanager or Member is blank", Constants.FAILURE, HttpStatus.BAD_REQUEST);
		}
		var mapping = memberCareManagerRepository.getByCareManagerAndMember(careManager, member);
		if (null == mapping) {
			throw new OmnichannelException("Invalid Caremanger and Member mapping", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "smsId");
			Page<SmsConversation> smsConversationPage = smsConversationRepository
					.getSmsConversationByUserIdAndMemberId(careManager, member, pageable);
			List<SmsConversation> smsConversationList = new ArrayList<>(smsConversationPage.getContent());
			List<SmsConversation> conversationList = new ArrayList<>(smsConversationPage.getContent());
			conversationList.forEach(smsConversation -> {
				if (Constants.RECEIVE.equalsIgnoreCase(smsConversation.getMessageType())
						&& 1 == smsConversation.getMessageStatus()) {
					smsConversation.setMessageStatus(2);
					smsConversationRepository.save(smsConversation);
				}
			});
			Collections.reverse(smsConversationList);

			smsConversationList.forEach(smsConversation -> {
				try {

					if (null != clientOffset) {
						long serverOffset = DateTimeUtil.getServerOffset(new Date());
						smsConversation.setCreatedOn(
								DateTimeUtil.calculateDate(serverOffset, clientOffset, smsConversation.getCreatedOn()));
					}

				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				smsConversation.setDeliveryStatus(
						deliveryStatusRepository.getLatestStatus(Arrays.asList(smsConversation.getSid())));
			});
			smsConversations = SmsConversations.builder().smsConversations(smsConversationList)
					.totalPages(smsConversationPage.getTotalPages()).totalItems(smsConversationPage.getTotalElements())
					.currentPage(smsConversationPage.getNumber()).size(smsConversationPage.getSize()).build();
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return smsConversations;
	}

	@Override
	public List<Template> getTemplates() {
		return templateRepository.findAll();
	}

	private SmsConversation createSMSConversation(UserMaster receiver, UserMaster sender, Message message) {

		return SmsConversation.builder().userId(sender.getUserId()).senderFullName(getFullName(sender))
				.memberId(receiver.getUserId()).receiverFullName(getFullName(receiver))
				.fromNumber(message.getFrom().toString()).toNumber(message.getTo()).body(message.getBody())
				.messageType(Constants.SEND).numSegments(message.getNumSegments())
				.direction(message.getDirection().toString()).price(message.getPrice())
				.errorMessage(message.getErrorMessage()).accountSid(message.getAccountSid())
				.numMedia(message.getNumMedia()).errorCode(String.valueOf(message.getErrorCode())).sid(message.getSid())
				.createdBy(sender.getUserId()).createdOn(new Date()).messageStatus(1).build();

	}

	private UserMaster getUserDetails(UserMaster userMaster) throws OmnichannelException {

		UserMaster userMasterDao = userMasterRepository.getUserMasterByUserIdAndUserType(userMaster.getUserId(),
				userMaster.getUserType());

		if (null == userMasterDao) {
			userMasterDao = createUser(userMaster);
		}

		return userMasterDao;
	}

	public MemberCareManagerMapping getMemberCareManagerMapping(UserMaster member, UserMaster careManager)
			throws OmnichannelException {

		if(null == member || null == careManager ){
			return null;
		}

		List<MemberCareManagerMapping> mappings = memberCareManagerRepository.findByMember(member.getUserId());
		log.debug("mappings:: {}", mappings);
		MemberCareManagerMapping memberCareManagerMapping;
		if (null != mappings && !mappings.isEmpty()) {
			memberCareManagerMapping = mappings.stream()
					.filter(mapping -> careManager.getUserId().equalsIgnoreCase(mapping.getCareManager())).findAny()
					.orElse(null);
			if (null != memberCareManagerMapping) {
				return memberCareManagerMapping;
			}

		}
		List<String> phonesNumbers = twilioSMSOperationService
				.fetchTwilioPhonesNumbers(configProperties.getMessageSid());

		if (null != mappings && !mappings.isEmpty()) {
			phonesNumbers
					.removeAll(mappings.stream().map(MemberCareManagerMapping::getCareManagerPhoneNumber).toList());
		}
		memberCareManagerMapping = MemberCareManagerMapping.builder().member(member.getUserId())
				// Remove characters space - () from phone number
				.memberPhoneNumber(member.getPhoneNumber().replaceAll("[()\\-\\s]", ""))
				.memberFullName(getFullName(member))
				.memberEmailId(member.getEmailId())
				.careManager(careManager.getUserId())
				.careManagerFullName(getFullName(careManager))
				.careManagerEmailId(careManager.getEmailId())
				.active(Boolean.TRUE).createdOn(new Date())
				.modifiedOn(new Date())
				.createdBy(Constants.ADMIN)
				.modifiedBy(Constants.ADMIN).build();
		memberCareManagerMapping.setModifiedOn(new Date());
		if (phonesNumbers.isEmpty()) {
			throw new OmnichannelException("All Twilio Numbers are consumed !!!", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			memberCareManagerMapping
					.setCareManagerPhoneNumber(phonesNumbers.get(new Random().nextInt(phonesNumbers.size())));
		}

		memberCareManagerRepository.save(memberCareManagerMapping);
		return memberCareManagerMapping;
	}

	@Override
	public List<MemberPreference> getMemberPreference(String member) throws OmnichannelException {
		List<MemberChannelOptinStatus> optinStatusList = new ArrayList<>();
		List<MemberPreference> memberPreferenceList = new ArrayList<>();
		if (StringUtils.isBlank(member)) {
			throw new OmnichannelException("Member is blank or null", Constants.FAILURE, HttpStatus.BAD_REQUEST);
		}
		optinStatusList = memberChannelOptinRepository.getMemberPreference(member.trim());

		if (null == optinStatusList) {
			throw new OmnichannelException(member + ": member no preference is set", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}

		optinStatusList.forEach(optin -> {

			memberPreferenceList.add(MemberPreference.builder().channelType(optin.getChannelType())
					.optinStatus(optin.getOptinStatus()).build());

		});
		return memberPreferenceList;
	}

	private String getFullName(UserMaster userMaster) {

		StringJoiner receiverFullName = new StringJoiner("");
		if (!StringUtils.isBlank(userMaster.getLastName())) {
			receiverFullName.add(userMaster.getLastName());
		}
		receiverFullName.add(", ");
		if (!StringUtils.isBlank(userMaster.getFirstName())) {

			receiverFullName.add(userMaster.getFirstName());
			receiverFullName.add(" ");
		}
		if (!StringUtils.isBlank(userMaster.getMiddleName())) {
			receiverFullName.add(userMaster.getMiddleName());
		}

		return receiverFullName.toString();
	}

	public UserMaster createUser(UserMaster usermaster) throws OmnichannelException {

		if (StringUtils.isEmpty(usermaster.getUserId())) {
			throw new OmnichannelException("USERID is required.", Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Check for special characters in userId
		/*if (!usermaster.getUserId().matches("^[a-zA-Z0-9_]*$")) {
			throw new OmnichannelException("User ID can only contain letters,numbers and underscore.",
					Constants.FAILURE, HttpStatus.NOT_FOUND);
		}*/

		// Check if UserId is already in use
		if (userMasterRepository.findByUserId(usermaster.getUserId()) != null) {
			throw new OmnichannelException("User ID already exists.", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (StringUtils.isEmpty(usermaster.getUserType()) || (!usermaster.getUserType().equals(Constants.MEMBER)
				&& !usermaster.getUserType().equals(Constants.CARE_MANAGER))) {

			throw new OmnichannelException(
					"User type is required & User type must be either 'MEMBER' or 'CARE_MANAGER'.", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		usermaster.setActive(true);
		usermaster.setCreatedOn(new Date());
		MemberChannelOptinStatus member = new MemberChannelOptinStatus();
		if (!usermaster.getEmailId().isEmpty()) {
			member = MemberChannelOptinStatus.builder().channelType(Constants.EMAIL).optinStatus(Constants.ACCEPTED)
					.member(usermaster.getUserId()).createdOn(new Date()).build();
			memberChannelOptinRepository.save(member);
		}
		return userMasterRepository.save(usermaster);
	}

	@Override
	public MemberCareManagerMapping createMemberCareManagerMapping(MemberCareManagerRequest request)
			throws OmnichannelException {
		UserMaster member = getUserDetails(request.getMember());

		UserMaster careManager = getUserDetails(request.getCareManager());

		return getMemberCareManagerMapping(member, careManager);
	}

	public MemberCareManagerMapping updateMemberCareManagerMapping(MemberCareManagerMapping memberCareManagerMapping)
			throws OmnichannelException {

		if (StringUtils.isEmpty(memberCareManagerMapping.getMember())
				|| StringUtils.isEmpty(memberCareManagerMapping.getCareManager())) {
			throw new OmnichannelException("Missing required parameter(s) for member or care manager",
					Constants.FAILURE, HttpStatus.BAD_REQUEST);
		}

		String member = memberCareManagerMapping.getMember();
		String careManager = memberCareManagerMapping.getCareManager();

		List<MemberCareManagerMapping> mappings = memberCareManagerRepository.findByMember(member);

		if (mappings == null || mappings.isEmpty()) {
			throw new OmnichannelException("Mapping not found for the specified member and care manager",
					Constants.FAILURE, HttpStatus.NOT_FOUND);
		}

		MemberCareManagerMapping existingMapping = mappings.stream()
				.filter(mapping -> careManager.equalsIgnoreCase(mapping.getCareManager())).findFirst().orElseThrow(
						() -> new OmnichannelException("Mapping not found for the specified member and care manager",
								Constants.FAILURE, HttpStatus.NOT_FOUND));

		String memberPhoneNumber = memberCareManagerMapping.getMemberPhoneNumber();

		// Check if the phone number is already in use by another mapping
		List<MemberCareManagerMapping> mappingsWithPhoneNumber = memberCareManagerRepository
				.findByMemberPhoneNumber(memberPhoneNumber);
		if (mappingsWithPhoneNumber != null && !mappingsWithPhoneNumber.isEmpty()) {
			// If there are other mappings with the same phone number, throw an exception
			throw new OmnichannelException("Phone number is already in use for another member", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		if (!memberPhoneNumber.matches("[0-9]+")) {
			throw new OmnichannelException("Phone number should consist only of numbers", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}

		existingMapping.setMemberPhoneNumber(memberPhoneNumber);

		MemberCareManagerMapping updatedMapping = memberCareManagerRepository.save(existingMapping);

		return updatedMapping;
	}

	@Override
	public UserMaster updateUser(Usermaster userMaster) throws OmnichannelException {
		UserMaster user = null;
		try {
			if (!Constants.MEMBER.equalsIgnoreCase(userMaster.getUserType())) {
				throw new OmnichannelException("Mapping not found for the specified member and care manager",
						Constants.FAILURE, HttpStatus.NOT_FOUND);
			}
			List<MemberCareManagerMapping> mappings = memberCareManagerRepository.findByMember(userMaster.getUserId());
			if (mappings == null) {
				throw new OmnichannelException("Mapping not found for the specified member and care manager",
						Constants.FAILURE, HttpStatus.NOT_FOUND);
			}
			user = userMasterRepository.findByUserId(userMaster.getUserId());
			if (null == user) {

				throw new OmnichannelException(user + ": user doesn't exist", Constants.FAILURE,
						HttpStatus.BAD_REQUEST);
			}
			if (StringUtils.isNotBlank(userMaster.getPhoneNumber())) {
				user.setPhoneNumber(userMaster.getPhoneNumber());
			}
			if (StringUtils.isNotBlank(userMaster.getEmailId())) {
				user.setEmailId(userMaster.getEmailId());
			}
			userMasterRepository.save(user);

			if (StringUtils.isNotBlank(userMaster.getPhoneNumber())
					|| StringUtils.isNotBlank(userMaster.getEmailId())) {
				mappings.stream().forEach(mapping -> {
					userMaster.getUserId().equalsIgnoreCase(mapping.getMember());
					mapping.setMemberEmailId(userMaster.getEmailId());
					mapping.setMemberPhoneNumber(userMaster.getPhoneNumber());
					memberCareManagerRepository.save(mapping);
				});
			}

		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException("Exception occures while updating userMaster", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		return user;
	}

	@Override
	public MemberChannelOptinStatus updatePreference(MemberPreference memberPreference, String member)
			throws OmnichannelException {
		MemberChannelOptinStatus channelOptinStatus = null;

		UserMaster userMaster = userMasterRepository.findByUserId(member);

		if (StringUtils.isEmpty(memberPreference.getChannelType())) {
			throw new OmnichannelException("Channeltype is not set", Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		if (userMaster != null) {
			MemberChannelOptinStatus memberChannelOptinStatus = MemberChannelOptinStatus.builder()
					.member(userMaster.getUserId()).channelType(memberPreference.getChannelType())
					.optinStatus(memberPreference.getOptinStatus()).createdOn(new Date()).build();
			channelOptinStatus = memberChannelOptinRepository.save(memberChannelOptinStatus);
		} else {
			throw new OmnichannelException("member does not exist", Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		return channelOptinStatus;
	}

}
