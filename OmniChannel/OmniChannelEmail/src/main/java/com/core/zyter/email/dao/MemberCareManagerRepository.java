package com.core.zyter.email.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.core.zyter.email.entities.MemberCareManagerMapping;

@Repository
public interface MemberCareManagerRepository extends JpaRepository<MemberCareManagerMapping, Long> {
	MemberCareManagerMapping getByCareManagerAndMember(String careManager, String member);

    @Query(value = "SELECT * FROM member_care_manager_mapping where CARE_MANAGER IN (:careManager)", nativeQuery = true)
   List<MemberCareManagerMapping> getByCareManagerEmailId(@Param("careManager") String careManager);
    

	@Query(value = "SELECT * FROM member_care_manager_mapping where MEMBER_EMAIL_ID IN (:emailId)", nativeQuery = true)
	MemberCareManagerMapping getByMemberEmailId(@Param("emailId") String EmailId);

}
