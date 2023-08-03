/*
 * @MemberChannelOptinRepository.java@
 * Created on 03Jan2023
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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.core.zyter.entites.MemberChannelOptinStatus;

public interface MemberChannelOptinRepository extends JpaRepository<MemberChannelOptinStatus, String> {

	@Query(value = "SELECT OPTIN_STATUS FROM member_channel_optin_mapping WHERE MEMBER=?1 ORDER BY USER_OPTIN_ID DESC LIMIT 1", nativeQuery = true)
	public String getMemberStatus(String member);

	@Query(value = "SELECT * from member_channel_optin_mapping T where T.MEMBER = :member AND EXISTS (SELECT * FROM member_channel_optin_mapping AS MC WHERE MC.MEMBER=T.MEMBER GROUP BY MC.CHANNEL_TYPE having T.CREATED_ON=MAX(MC.CREATED_ON))", nativeQuery = true)
	public List<MemberChannelOptinStatus> getMemberPreference(String member);
}