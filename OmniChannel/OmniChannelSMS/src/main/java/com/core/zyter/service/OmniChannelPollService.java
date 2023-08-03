/*
 * @OmniChannelPollService.java@
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

package com.core.zyter.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.core.zyter.bulk.vos.TemplateTypeResponse;
import com.core.zyter.bulk.vos.UnreadMessageCount;
import com.core.zyter.entites.DeliveryStatus;
import com.core.zyter.entites.SmsConversation;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.vos.MappingRequest;
import com.core.zyter.vos.MappingResponse;

public interface OmniChannelPollService {

    List<SmsConversation> getReceiveMessages(String date, String careManager, String member, Long clientOffset) throws ParseException, OmnichannelException;

    List<DeliveryStatus> getDeliveryStatus(List<String> smsId) throws OmnichannelException;
    UnreadMessageCount getUnreadMessageCount(String careManager, String member) throws ParseException, OmnichannelException;

	TemplateTypeResponse templateFormat(String name, String type, String mode) throws OmnichannelException, IOException;
	
	MappingResponse phoneNumberAndEMailIdValidation(MappingRequest mappingRequest) throws OmnichannelException;
}
