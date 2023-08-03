package com.core.zyter.sms.service;

import java.util.Date;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.TwilioSMSOperationService;
import com.core.zyter.sms.dao.SMSDeliveryStatusRepository;
import com.core.zyter.sms.dao.SMSRepository;
import com.core.zyter.sms.entities.SMSDeliveryStatus;
import com.core.zyter.sms.entities.SMSHistory;
import com.core.zyter.sms.vos.SMSVo;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import com.twilio.rest.api.v2010.account.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

	@Autowired
	TwilioSMSOperationService twilioSMSOperationService;

	@Autowired
	SMSRepository smsRepository;
	@Autowired
	SMSDeliveryStatusRepository smsDeliveryStatusRepository;
	@Autowired
	ConfigProperties configProperties;

	@Override
	public SMSHistory sendMessage(SMSVo smsVo) throws OmnichannelException {
		SMSHistory sendSMS = new SMSHistory();
		try {

			List<String> phoneNumberList = twilioSMSOperationService
					.fetchTwilioPhonesNumbers(configProperties.getGenericMessageSid());

			String fromNumber = phoneNumberList.get(new Random().nextInt(phoneNumberList.size()));
			Message message = twilioSMSOperationService.sendMessage(fromNumber, smsVo.getToPhoneNumber(),
					smsVo.getMsg());
			sendSMS = createSMSConversation(message, smsVo);
			log.info("SendSMS: {}", sendSMS);

			smsRepository.save(sendSMS);
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return sendSMS;
	}

	private SMSHistory createSMSConversation(Message message, SMSVo smsVo) {

		return SMSHistory.builder().userId(smsVo.getFrom()).memberId(smsVo.getTo())
				.fromNumber(message.getFrom().toString()).toNumber(message.getTo().toString()).body(message.getBody())
				.messageType(Constants.SEND).numSegments(message.getNumSegments())
				.direction(message.getDirection().toString()).price(message.getPrice())
				.errorMessage(message.getErrorMessage()).accountSid(message.getAccountSid())
				.numMedia(message.getNumMedia()).errorCode(String.valueOf(message.getErrorCode())).sid(message.getSid())
				.createdBy("Admin").createdOn(new Date()).serviceType(smsVo.getType()).build();

	}

	@Override
	public String receiveDeliveryStatus(SMSDeliveryStatus smsOTPDeliveryStatus) {
		try {
			smsDeliveryStatusRepository.save(smsOTPDeliveryStatus);
		} catch (Exception e) {
			log.error("", e);
			return Constants.FAILURE;
		}
		return Constants.SUCCESS;
	}

}
