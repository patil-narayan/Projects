package com.core.zyter.email.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.zyter.email.entities.EmailHistory;

@Repository
public interface EmailRepository extends JpaRepository<EmailHistory, Long>{
	
	Page<EmailHistory> getByUserIdAndMemberId(String careManager,String member, Pageable pageable);
	
	

}
