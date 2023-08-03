/*
 * @TwilioSMSRepository.java@
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

package com.core.zyter.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.zyter.entites.SmsConversation;


@Repository
public interface SMSConversationRepository extends JpaRepository<SmsConversation, Long> {

    Page<SmsConversation> getSmsConversationByUserIdAndMemberId(String userId, String memberId, Pageable pageable);

	@Query(value = "SELECT count(1) FROM sms_conversation "
			+ "WHERE USER_ID = ?1 AND MEMBER_ID = ?2 AND MESSAGE_TYPE = 'RECEIVE' AND MESSAGE_STATUS = 1 ", nativeQuery = true)
	Integer getUnreadMessageCount(String caremanager, String member);

    @Query(value ="SELECT * FROM sms_conversation "  +
            "WHERE CREATED_ON > ?1 AND USER_ID = ?2 AND MEMBER_ID = ?3 AND MESSAGE_TYPE = 'RECEIVE' AND MESSAGE_STATUS = 1 ", nativeQuery = true)
    Optional<List<SmsConversation>> getReceiveSMS(Date date, String careManager, String member);
                                                  
}
