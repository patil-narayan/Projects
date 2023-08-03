package com.core.zyter.email.service.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.dao.EmailEventDetailsRepository;
import com.core.zyter.email.dao.EmailRepository;
import com.core.zyter.email.dao.EmailStatus;
import com.core.zyter.email.dao.EmailTemplateRepository;
import com.core.zyter.email.dao.MemberCareManagerRepository;
import com.core.zyter.email.dao.MemberChannelOptinRepository;
import com.core.zyter.email.entities.EmailDeliveryStatus;
import com.core.zyter.email.entities.EmailEventDetails;
import com.core.zyter.email.entities.EmailHistory;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.entities.MemberCareManagerMapping;
import com.core.zyter.email.entities.MemberChannelOptinStatus;
import com.core.zyter.email.service.EmailService;
import com.core.zyter.email.util.Constants;
import com.core.zyter.email.util.DateTimeUtil;
import com.core.zyter.email.util.OmniChannelEmailUtil;
import com.core.zyter.email.vos.DynamicPlaceHolder;
import com.core.zyter.email.vos.EmailHistoryVos;
import com.core.zyter.email.vos.EmailRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	SendGrid sendgrid;

	@Autowired
	MemberCareManagerRepository memberCareManagerRepository;
	@Autowired
	EmailTemplateRepository emailTemaplateRepository;
	@Autowired
	EmailRepository emailRepository;
	@Autowired
	EmailEventDetailsRepository emailEventDetailsRepository;
	@Autowired
	MemberChannelOptinRepository memberChannelOptinRepository;
	@Autowired
	EmailStatus emailStatus;

	public EmailHistoryVos getEmailHistory(String careManager, String member, int page, int size, Long clientOffset)
			throws OmnichannelException {
		EmailHistoryVos emailHistoryVos;
		MemberCareManagerMapping mapping = memberCareManagerRepository.getByCareManagerAndMember(careManager, member);
		if (null == mapping) {
			throw new OmnichannelException("CareManager or Member is not present Please check", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
		Page<EmailHistory> emails = emailRepository.getByUserIdAndMemberId(careManager, member, pageable);
		List<EmailHistory> emailsList = new ArrayList<>(emails.getContent());
		Collections.reverse(emailsList);

		emailsList.forEach(emailConversation -> {
			try {
				if (null != clientOffset) {
					long serverOffset = DateTimeUtil.getServerOffset(new Date());
					emailConversation.setCreatedOn(
							DateTimeUtil.calculateDate(serverOffset, clientOffset, emailConversation.getCreatedOn()));
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		});

		emailHistoryVos = EmailHistoryVos.builder().emails(emailsList).totalPages(emails.getTotalPages())
				.size(emails.getSize()).totalItems(emails.getTotalElements()).currentPage(emails.getNumber()).build();

		return emailHistoryVos;
	}

	@Override
	public EmailHistory sendEmail(EmailRequest emailRequest, Long clientOffset) throws OmnichannelException {
		EmailHistory emailHistory;
		DynamicPlaceHolder dynamicPlaceHolder = null;
		Mail mail;

		MemberCareManagerMapping mapping = memberCareManagerRepository
				.getByCareManagerAndMember(emailRequest.getCareManager(), emailRequest.getMember());
		if (mapping == null) {
			throw new OmnichannelException("No mapping found for care manager " + emailRequest.getCareManager()
					+ " and member " + emailRequest.getMember(), Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		List<MemberChannelOptinStatus> memberChannelOptinStatus = memberChannelOptinRepository
				.findByMemberAndChannelType(emailRequest.getMember(), Constants.EMAIL);
		Collections.reverse(memberChannelOptinStatus);
		if (!memberChannelOptinStatus.isEmpty() && memberChannelOptinStatus != null) {
			if (memberChannelOptinStatus.get(0).getChannelType().equalsIgnoreCase(Constants.EMAIL)
					&& memberChannelOptinStatus.get(0).getOptinStatus().equalsIgnoreCase(Constants.DECLINED)) {
				throw new OmnichannelException(
						"Member " + memberChannelOptinStatus.get(0).getMember() + " is unsubscribed for email service ",
						Constants.FAILURE, HttpStatus.NOT_FOUND);
			}
		}
		if (StringUtils.isBlank(mapping.getCareManagerEmailId()) || StringUtils.isBlank(mapping.getMemberEmailId())) {
			throw new OmnichannelException("Email not found for member / careManager ", Constants.FAILURE,
					HttpStatus.NOT_FOUND);
		}
		if (StringUtils.isEmpty(emailRequest.getTemplateId()) || StringUtils.isBlank(emailRequest.getMember())
				|| StringUtils.isBlank(emailRequest.getCareManager())) {
			throw new OmnichannelException("TemplateId/memberId/careManagerId cannot be null or empty.",
					Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		Optional<EmailTemplate> emailTemplates = emailTemaplateRepository.findById(emailRequest.getTemplateId());
		if (emailTemplates.isEmpty() || !emailTemplates.get().isActive()) {
			throw new OmnichannelException(
					"Email template not found or is not active for ID " + emailRequest.getTemplateId(),
					Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		try {
			Path filePath = Paths.get(emailTemplates.get().getFilePath());
			byte[] fileContent = Files.readAllBytes(filePath);
			String emailBody = new String(fileContent, StandardCharsets.UTF_8);
			String emailSubject = emailTemplates.get().getSubject();

			if (emailRequest.getPlaceholders() != null) {
				dynamicPlaceHolder = OmniChannelEmailUtil.placeHolderForEmail(emailBody, emailSubject,
						emailRequest.getPlaceholders());
				mail = new Mail(new Email(mapping.getCareManagerEmailId()), dynamicPlaceHolder.getEmailSubject(),
						new Email(mapping.getMemberEmailId()),
						new Content(Constants.FILE_TYPE_HTML, dynamicPlaceHolder.getTemplateContent()));

				emailHistory = EmailHistory.builder().userId(mapping.getCareManager()).memberId(mapping.getMember())
						.subject(dynamicPlaceHolder.getEmailSubject()).fromEmail(mapping.getCareManagerEmailId())
						.body(dynamicPlaceHolder.getTemplateContent()).createdOn(new Date())
						.toEmail(mapping.getMemberEmailId()).build();
			} else {
				mail = new Mail(new Email(mapping.getCareManagerEmailId()), emailSubject,
						new Email(mapping.getMemberEmailId()), new Content(Constants.FILE_TYPE_HTML, emailBody));

			}
			Response response = null;
			Request sendGridRequest = new Request();
			sendGridRequest.setMethod(Method.POST);
			sendGridRequest.setEndpoint("mail/send");
			sendGridRequest.setBody(mail.build());
			response = sendgrid.api(sendGridRequest);
			String message_id = response.getHeaders().get("X-Message-Id");

			// Save the email details to the database
			emailHistory = EmailHistory.builder().userId(mapping.getCareManager()).memberId(mapping.getMember())
					.subject(emailSubject).fromEmail(mapping.getCareManagerEmailId()).body(emailBody)
					.createdOn(new Date()).toEmail(mapping.getMemberEmailId()).messageId(message_id).build();
			emailRepository.save(emailHistory);

			try {
				if (null != clientOffset) {
					long serverOffset = DateTimeUtil.getServerOffset(new Date());
					emailHistory.setCreatedOn(
							DateTimeUtil.calculateDate(serverOffset, clientOffset, emailHistory.getCreatedOn()));
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException("Exception occurred while sending Email ", Constants.FAILURE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return emailHistory;
	}

	@Override
	public String deliveryEvent(List<EmailEventDetails> payload) throws OmnichannelException {
		EmailDeliveryStatus emailDeliveryStatus = null;
		try {
			emailEventDetailsRepository.saveAll(payload);
			for (EmailEventDetails str : payload) {
				String msg = str.getSg_message_id();
				String[] output = msg.split("\\.");
				if (Constants.DELIVERED.equalsIgnoreCase(str.getEvent().trim())) {
					emailDeliveryStatus = EmailDeliveryStatus.builder().messageId(output[0]).emailStatus(str.getEvent())
							.createdOn(new Date()).build();
					emailStatus.save(emailDeliveryStatus);
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException("email not delivered", Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return Constants.SUCCESS;
	}
}
