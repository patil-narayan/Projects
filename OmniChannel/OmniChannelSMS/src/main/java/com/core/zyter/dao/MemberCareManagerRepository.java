/*
 * @OmniChannelSMSRepository.java@
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

import com.core.zyter.entites.MemberCareManagerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberCareManagerRepository extends JpaRepository<MemberCareManagerMapping, Long> {
	MemberCareManagerMapping findByMemberAndCareManager(String member, String careManager);

	List<MemberCareManagerMapping> findByMember(String member);

	MemberCareManagerMapping getByMemberPhoneNumberAndCareManagerPhoneNumber(String memberPhoneNumber,
			String careManagerPhoneNumber);

	MemberCareManagerMapping getByCareManagerAndMember(String careManager, String member);
	
	List<MemberCareManagerMapping> findByMemberPhoneNumber(String memberPhoneNumber);

}
