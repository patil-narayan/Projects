/*
 * @OmniChannelPollServiceImpl.java@
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

package com.core.zyter.service.impl;

import com.core.zyter.bulk.vos.TemplateTypeResponse;
import com.core.zyter.bulk.vos.UnreadMessageCount;
import com.core.zyter.dao.DeliveryStatusRepository;
import com.core.zyter.dao.MemberCareManagerRepository;
import com.core.zyter.dao.ReceiveSMSRepository;
import com.core.zyter.dao.SMSConversationRepository;
import com.core.zyter.dao.TemplateTypeRepository;
import com.core.zyter.entites.DeliveryStatus;
import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.SmsConversation;
import com.core.zyter.entites.TemplateType;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.OmniChannelPollService;
import com.core.zyter.util.Constants;
import com.core.zyter.util.DateTimeUtil;
import com.core.zyter.vos.MappingRequest;
import com.core.zyter.vos.MappingResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OmniChannelPollServiceImpl implements OmniChannelPollService {

    @Autowired
    ReceiveSMSRepository receiveSMSRepository;
    @Autowired
    DeliveryStatusRepository deliveryStatusRepository;
    @Autowired
    MemberCareManagerRepository memberCareManagerRepository;
    @Autowired
    TemplateTypeRepository templateTypeRepository;
    @Autowired
    SMSConversationRepository smsConversationRepository;

   // @Transactional
    @Override
    public List<SmsConversation> getReceiveMessages(String dateString, String careManager, String member, Long clientOffset) throws OmnichannelException {

        if (StringUtils.isBlank(careManager) || StringUtils.isBlank(member)) {
            throw new OmnichannelException("Caremanager or Member is blank", Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        var mapping = memberCareManagerRepository.getByCareManagerAndMember(careManager, member);
        if (null == mapping) {
            throw new OmnichannelException("Invalid Caremanger and Member mapping", Constants.FAILURE,
                    HttpStatus.BAD_REQUEST);
        }
        List<SmsConversation> receivelist = null;
        List<SmsConversation> receiveMessages = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(dateString);
            Optional<List<SmsConversation>> receiveSMSList = smsConversationRepository.getReceiveSMS(date,
                    mapping.getCareManager(), mapping.getMember());
            receivelist = new ArrayList<>(receiveSMSList.get());
            receiveMessages = new ArrayList<>(receiveSMSList.get());
            if (receiveSMSList.isPresent() && !receiveSMSList.isEmpty()) {

                receivelist.forEach(smsConversation -> {
                    smsConversation.setMessageStatus(2);
                    smsConversation.setDeliveryStatus(null);
                });
                smsConversationRepository.saveAll(receivelist);
				/**
				 * Timezone conversion
				 */
                receiveMessages.forEach(smsConversation -> {
                    try {
                        if (null != clientOffset) {
                            long serverOffset = DateTimeUtil.getServerOffset(new Date());
                            smsConversation.setCreatedOn(DateTimeUtil.calculateDate(serverOffset, clientOffset, smsConversation.getCreatedOn()));
                        }
                    } catch (ParseException e) {
                        log.error("Exception occurred during timezone conversion", e);
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return receiveMessages;
    }

    @Override
    public List<DeliveryStatus> getDeliveryStatus(List<String> smsId) throws OmnichannelException {
        List<DeliveryStatus> latestStatus;
        try {

            latestStatus = deliveryStatusRepository.getLatestStatus(smsId);
        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return latestStatus;
    }

    @Override
    public UnreadMessageCount getUnreadMessageCount(String careManager, String member) throws OmnichannelException {
        var mapping = memberCareManagerRepository.getByCareManagerAndMember(careManager, member);
        if (null == mapping) {
				/*throw new OmnichannelException("Invalid Caremanger and Member mapping", Constants.FAILURE,
						HttpStatus.BAD_REQUEST);*/
            return UnreadMessageCount.builder().unreadMsg(0).build();
        }
        Integer message = smsConversationRepository.getUnreadMessageCount(mapping.getCareManager(), mapping.getMember());
        UnreadMessageCount unreadMessage = UnreadMessageCount.builder().unreadMsg(message).build();

        return unreadMessage;
    }

    @Override
    public TemplateTypeResponse templateFormat(String name, String type, String mode) throws OmnichannelException, IOException {

        Optional<TemplateType> templateType = templateTypeRepository.getByNameAndTypeAndMode(name, type, mode);
        if (templateType.isEmpty()) {
            throw new OmnichannelException(String.format("Template name: %s does not exist", name), Constants.FAILURE,
                    HttpStatus.NOT_FOUND);
        }

        Path fileName = Path.of(templateType.get().getFilepath());
        String template = Files.readString(fileName);
        TemplateTypeResponse templateResponse = TemplateTypeResponse.builder().content(template).templateType(templateType).build();

        return templateResponse;
    }

    @Override
    public MappingResponse phoneNumberAndEMailIdValidation(MappingRequest mappingRequest) throws OmnichannelException {
        MappingResponse mappingResponse = new MappingResponse();
        MemberCareManagerMapping mapping = memberCareManagerRepository.getByCareManagerAndMember(mappingRequest.getCareManager(), mappingRequest.getMember());
        if (mapping == null) {
            throw new OmnichannelException("Memmber Not Mapped to CareMannager", Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        mappingResponse.setCareManagerPhoneNumberExist(StringUtils.isNotBlank(mapping.getCareManagerPhoneNumber()) ? true : false);
        mappingResponse.setMemberPhoneNumberExist(StringUtils.isNotBlank(mapping.getMemberPhoneNumber()) ? true : false);
        mappingResponse.setCareManagerEmailIdExist(StringUtils.isNotBlank(mapping.getCareManagerEmailId()) ? true : false);
        mappingResponse.setMemberEmailIdExist(StringUtils.isNotBlank(mapping.getMemberEmailId()) ? true : false);

        return mappingResponse;
    }


}
	
