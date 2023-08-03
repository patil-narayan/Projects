/*
 * @OmniChannelSMSService.java@
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

import java.util.List;

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
import com.core.zyter.vos.MemberCareManagerRequest;
import com.core.zyter.vos.MemberPreference;
import com.core.zyter.vos.SmsConversations;
import com.core.zyter.vos.Usermaster;

public interface OmniChannelSMSService {

	List<UserMaster> getAllUsers();

	int addMessage(String notifySid, List<User> users);

	String receiveDeliveryStatus(DeliveryStatus twilioSMSDeliveryStatus);

	SmsConversation receiveMessage(ReceiveSMS twilioReceiveSMS, String twilioSignature) throws OmnichannelException;

	SmsConversation sendMessage(OmnichannelSMS omnichannelSMS, Long clientOffset) throws OmnichannelException;

	SmsConversation sendTemplate(OmnichannelSMS omnichannelSMS, String type, Long clientOffset)
			throws OmnichannelException;

	void fetchTwilioPhonesNumbers();

	SmsConversations getSMSConversation(String userId, String memberId, int offset, int limit, Long clientOffset)
			throws OmnichannelException;

	List<Template> getTemplates();

	List<MemberPreference> getMemberPreference(String member) throws OmnichannelException;

	UserMaster createUser(UserMaster userMaster) throws OmnichannelException;

	MemberCareManagerMapping getMemberCareManagerMapping(UserMaster member, UserMaster careManagermember)
			throws OmnichannelException;

	MemberCareManagerMapping updateMemberCareManagerMapping(MemberCareManagerMapping memberCareManagerMapping)
			throws OmnichannelException;

	UserMaster updateUser(Usermaster usermaster) throws OmnichannelException;

	MemberCareManagerMapping createMemberCareManagerMapping(MemberCareManagerRequest request)
			throws OmnichannelException;
	
	MemberChannelOptinStatus updatePreference(MemberPreference memberPreference, String member)
			throws OmnichannelException;

}
