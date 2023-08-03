/*
 * @TwilioSMSOperationService.java@
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

package com.core.zyter.service;

import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.User;
import com.core.zyter.exceptions.OmnichannelException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.notify.v1.service.Notification;

import java.util.List;
import java.util.Map;

public interface TwilioSMSOperationService {

	Notification notifySMS(String messageBody, boolean saveMessage, List<User> users);

	public Message sendMessage(MemberCareManagerMapping memberCareManager, String messageStr);

	List<String> fetchTwilioPhonesNumbers(String messageSid);

	public Message sendMessage(String fromNumber, String toNumber, String msg);

	public void deleteMessage(String messageSid) throws OmnichannelException;

	void save(Map<String, String> payload) throws Exception;

}
